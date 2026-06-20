package com.boss.matching.service;

import com.boss.matching.config.AppProperties;
import com.boss.matching.domain.Influencer;
import com.boss.matching.domain.Merchant;
import com.boss.matching.domain.User;
import com.boss.matching.domain.UserRole;
import com.boss.matching.infra.cache.CacheService;
import com.boss.matching.persistence.MarketplaceStore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Handles authentication and session creation for phone-code and WeChat login flows.
 */
@Service
public class AuthService {
    private static final String DEFAULT_MERCHANT_NAME = "\u65b0\u5546\u5bb6";
    private static final String DEFAULT_INFLUENCER_NAME = "\u65b0\u8fbe\u4eba";
    private static final String INCOMPLETE_TEXT = "\u5f85\u5b8c\u5584";
    private static final String LOCKED_TEXT = "\u4ed8\u8d39\u89e3\u9501\u540e\u53ef\u89c1";
    private static final String PUBLIC_INFLUENCER_CACHE_KEY = "influencer:public:list";

    private final MarketplaceStore store;
    private final CacheService cacheService;
    private final AuthTokenService authTokenService;
    private final AppProperties properties;
    private final WechatAuthService wechatAuthService;

    /**
     * Creates an authentication service backed by the configured marketplace store.
     *
     * @param store shared persistence abstraction
     * @param cacheService cache adapter for influencer visibility side effects
     * @param authTokenService JWT token issuer and verifier
     * @param properties application feature flags and auth configuration
     * @param wechatAuthService WeChat code exchange adapter
     */
    public AuthService(MarketplaceStore store, CacheService cacheService, AuthTokenService authTokenService, AppProperties properties, WechatAuthService wechatAuthService) {
        this.store = store;
        this.cacheService = cacheService;
        this.authTokenService = authTokenService;
        this.properties = properties;
        this.wechatAuthService = wechatAuthService;
    }

    /**
     * Creates a development-only mock session with a generated user and profile.
     *
     * @param role role to assign to the mock user
     * @param nickname optional display name for the generated profile
     * @return response map containing token, user, and profile
     */
    public Map<String, Object> mockLogin(UserRole role, String nickname) {
        if (!properties.isMockEnabled()) {
            throw new IllegalStateException("Mock login is only available in development.");
        }

        long userId = store.nextId();
        User user = new User(userId, role, "mock-openid-" + userId, Instant.now());
        store.saveUser(user);
        if (role == UserRole.MERCHANT) {
            Merchant merchant = new Merchant(store.nextId(), userId, defaultText(nickname, DEFAULT_MERCHANT_NAME), "\u7f8e\u5986", "\u6b63\u5728\u5bfb\u627e\u8fbe\u4eba\u63a8\u5e7f\u65b0\u54c1", "merchant@example.com");
            store.saveMerchant(merchant);
            return Map.of("token", authTokenService.createToken(user), "user", user, "profile", merchant);
        }

        Influencer influencer = new Influencer(store.nextId(), userId, defaultText(nickname, DEFAULT_INFLUENCER_NAME), "\u6296\u97f3", "1\u4e07-5\u4e07", 30000, "\u7f8e\u5986", "500-1000", "influencer@example.com", "@demo", false);
        store.saveInfluencer(influencer);
        cacheService.evict(PUBLIC_INFLUENCER_CACHE_KEY);
        return Map.of("token", authTokenService.createToken(user), "user", user, "profile", ownInfluencerProfile(influencer));
    }

    /**
     * Registers a phone-code user or returns an existing same-role session.
     *
     * @param request phone, verification code, and requested role
     * @return response map containing token, user, profile, and binding state
     */
    public Map<String, Object> register(AuthRegisterRequest request) {
        validateMockCode(request.code());

        Optional<User> existing = store.findUserByPhone(request.phone());
        if (existing.isPresent()) {
            User user = existing.get();
            if (user.role() != request.role()) {
                throw new IllegalArgumentException("Phone number has already registered another role.");
            }
            return createSession(user);
        }

        long userId = store.nextId();
        User user = new User(userId, request.role(), request.phone(), null, Instant.now());
        store.saveUser(user);
        return createSession(user);
    }

    /**
     * Logs in a phone-code user.
     *
     * @param request phone and verification code
     * @return response map containing token, user, profile, and binding state
     */
    public Map<String, Object> login(AuthLoginRequest request) {
        validateMockCode(request.code());

        User user = store.findUserByPhone(request.phone())
                .orElseThrow(() -> new IllegalArgumentException("Account does not exist. Please register first."));
        return createSession(user);
    }

    /**
     * Exchanges a WeChat login code for a local user session.
     *
     * @param request WeChat temporary login code
     * @return response map containing token, user, profile, and binding state
     */
    public Map<String, Object> wxLogin(WxLoginRequest request) {
        WechatAuthService.WechatSession session = wechatAuthService.code2Session(request.code());
        User user = store.findUserByOpenid(session.openid())
                .orElseGet(() -> {
                    User created = new User(store.nextId(), UserRole.UNBOUND, null, session.openid(), session.unionid(), "WECHAT", Instant.now());
                    store.saveUser(created);
                    return created;
                });
        return createSession(user);
    }

    /**
     * Binds an existing unbound WeChat user to a business role.
     *
     * @param userId authenticated user id from the bearer token
     * @param request selected role
     * @return response map containing token, user, profile, and binding state
     */
    public Map<String, Object> bindRole(long userId, BindRoleRequest request) {
        if (request.role() == UserRole.UNBOUND) {
            throw new IllegalArgumentException("Please select merchant or influencer role.");
        }

        User current = store.findUser(userId)
                .orElseThrow(() -> new IllegalArgumentException("User does not exist."));
        if (current.role() != UserRole.UNBOUND && current.role() != request.role()) {
            throw new IllegalArgumentException("Account has already bound another role.");
        }

        User updated = current.withRole(request.role());
        store.saveUser(updated);
        return createSession(updated);
    }

    private void validateMockCode(String code) {
        // The MVP uses a configured code; switching to SMS later should only replace this verification source.
        if (!properties.getAuth().getMockCode().equals(code)) {
            throw new IllegalArgumentException("Verification code is incorrect.");
        }
    }

    private Map<String, Object> createSession(User user) {
        Map<String, Object> session = new LinkedHashMap<>();
        session.put("token", authTokenService.createToken(user));
        session.put("needBindRole", user.role() == UserRole.UNBOUND);
        session.put("user", user);
        if (user.role() == UserRole.MERCHANT) {
            session.put("profile", ensureMerchantProfile(user));
        } else if (user.role() == UserRole.INFLUENCER) {
            session.put("profile", ownInfluencerProfile(ensureInfluencerProfile(user)));
        } else {
            session.put("profile", null);
        }
        return session;
    }

    private Merchant ensureMerchantProfile(User user) {
        return store.findMerchantByUserId(user.id())
                .orElseGet(() -> {
                    Merchant merchant = new Merchant(store.nextId(), user.id(), DEFAULT_MERCHANT_NAME, INCOMPLETE_TEXT, "\u8bf7\u5b8c\u5584\u54c1\u724c\u7b80\u4ecb", "");
                    store.saveMerchant(merchant);
                    return merchant;
                });
    }

    private Influencer ensureInfluencerProfile(User user) {
        return store.findInfluencerByUserId(user.id())
                .orElseGet(() -> {
                    Influencer influencer = new Influencer(store.nextId(), user.id(), DEFAULT_INFLUENCER_NAME, INCOMPLETE_TEXT, INCOMPLETE_TEXT, 0, INCOMPLETE_TEXT, INCOMPLETE_TEXT, "", "", false);
                    store.saveInfluencer(influencer);
                    cacheService.evict(PUBLIC_INFLUENCER_CACHE_KEY);
                    return influencer;
                });
    }

    private Map<String, Object> maskInfluencer(Influencer influencer) {
        Map<String, Object> value = new LinkedHashMap<>();
        value.put("id", influencer.id());
        value.put("nickname", influencer.nickname());
        value.put("avatarUrl", influencer.avatarUrl());
        value.put("city", influencer.city());
        value.put("platform", influencer.platform());
        value.put("fansRange", influencer.fansRange());
        value.put("category", influencer.category());
        value.put("categories", influencer.categories());
        value.put("styleTags", influencer.styleTags());
        value.put("contentForms", influencer.contentForms());
        value.put("priceRange", influencer.priceRange());
        value.put("priceImageText", influencer.priceImageText());
        value.put("priceVideo", influencer.priceVideo());
        value.put("isPublic", influencer.isPublic());
        value.put("unlocked", false);
        value.put("contact", LOCKED_TEXT);
        value.put("socialAccount", LOCKED_TEXT);
        value.put("contactWechat", LOCKED_TEXT);
        value.put("contactPhone", LOCKED_TEXT);
        value.put("priceDetail", LOCKED_TEXT);
        value.put("fansCount", null);
        value.put("portfolio", store.listInfluencerPortfolios(influencer.id()));
        return value;
    }

    private Map<String, Object> ownInfluencerProfile(Influencer influencer) {
        Map<String, Object> value = new LinkedHashMap<>();
        value.put("id", influencer.id());
        value.put("userId", influencer.userId());
        value.put("nickname", influencer.nickname());
        value.put("avatarUrl", influencer.avatarUrl());
        value.put("city", influencer.city());
        value.put("platform", influencer.platform());
        value.put("fansRange", influencer.fansRange());
        value.put("fansCount", influencer.fansCount());
        value.put("category", influencer.category());
        value.put("categories", influencer.categories());
        value.put("styleTags", influencer.styleTags());
        value.put("contentForms", influencer.contentForms());
        value.put("priceRange", influencer.priceRange());
        value.put("priceImageText", influencer.priceImageText());
        value.put("priceVideo", influencer.priceVideo());
        value.put("priceDetail", influencer.priceDetail());
        value.put("contactWechat", influencer.contactWechat());
        value.put("contactPhone", influencer.contactPhone());
        value.put("contact", influencer.contact());
        value.put("socialAccount", influencer.socialAccount());
        value.put("isPublic", influencer.isPublic());
        value.put("portfolio", store.listInfluencerPortfolios(influencer.id()));
        return value;
    }

    private String defaultText(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    /**
     * Request body for phone-code registration.
     *
     * @param phone phone number
     * @param code verification code
     * @param role selected user role
     */
    public record AuthRegisterRequest(@NotBlank String phone, @NotBlank String code, @NotNull UserRole role) {
    }

    /**
     * Request body for phone-code login.
     *
     * @param phone phone number
     * @param code verification code
     */
    public record AuthLoginRequest(@NotBlank String phone, @NotBlank String code) {
    }

    /**
     * Request body for WeChat login.
     *
     * @param code WeChat temporary login code
     */
    public record WxLoginRequest(@NotBlank String code) {
    }

    /**
     * Request body for role binding after WeChat login.
     *
     * @param role selected role, excluding {@link UserRole#UNBOUND}
     */
    public record BindRoleRequest(@NotNull UserRole role) {
    }
}
