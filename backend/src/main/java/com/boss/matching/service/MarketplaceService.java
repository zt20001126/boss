package com.boss.matching.service;

import com.boss.matching.config.AppProperties;
import com.boss.matching.domain.*;
import com.boss.matching.infra.cache.CacheService;
import com.boss.matching.payment.PaymentResult;
import com.boss.matching.payment.PaymentService;
import com.boss.matching.persistence.MarketplaceStore;
import com.boss.matching.persistence.MemoryMarketplaceStore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MarketplaceService {
    private static final String PUBLIC_INFLUENCER_CACHE_KEY = "influencer:public:list";

    private final MarketplaceStore store;
    private final CacheService cacheService;
    private final PaymentService paymentService;
    private final AuthTokenService authTokenService;
    private final AppProperties properties;
    private final WechatAuthService wechatAuthService;

    @Autowired
    public MarketplaceService(MarketplaceStore store, CacheService cacheService, PaymentService paymentService, AuthTokenService authTokenService, AppProperties properties, WechatAuthService wechatAuthService) {
        this.store = store;
        this.cacheService = cacheService;
        this.paymentService = paymentService;
        this.authTokenService = authTokenService;
        this.properties = properties;
        this.wechatAuthService = wechatAuthService;
        seed();
    }

    public MarketplaceService(CacheService cacheService, PaymentService paymentService, AuthTokenService authTokenService, AppProperties properties) {
        this(new MemoryMarketplaceStore(), cacheService, paymentService, authTokenService, properties, new WechatAuthService(properties));
    }

    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "mockEnabled", properties.isMockEnabled(),
                "cacheProvider", properties.getCache().getProvider(),
                "paymentProvider", properties.getPayment().getProvider()
        );
    }

    public Map<String, Object> mockLogin(UserRole role, String nickname) {
        if (!properties.isMockEnabled()) {
            throw new IllegalStateException("Mock 登录仅允许在开发环境启用");
        }

        long userId = store.nextId();
        User user = new User(userId, role, "mock-openid-" + userId, Instant.now());
        store.saveUser(user);
        if (role == UserRole.MERCHANT) {
            Merchant merchant = new Merchant(store.nextId(), userId, defaultText(nickname, "新商家"), "美妆", "正在寻找达人推广新品", "merchant@example.com");
            store.saveMerchant(merchant);
            return Map.of("token", authTokenService.createToken(user), "user", user, "profile", merchant);
        }

        Influencer influencer = new Influencer(store.nextId(), userId, defaultText(nickname, "新达人"), "抖音", "1w-5w", 30000, "美妆", "500-1000", "influencer@example.com", "@demo", false);
        store.saveInfluencer(influencer);
        cacheService.evict(PUBLIC_INFLUENCER_CACHE_KEY);
        return Map.of("token", authTokenService.createToken(user), "user", user, "profile", maskInfluencer(influencer, false));
    }

    public Map<String, Object> register(AuthRegisterRequest request) {
        validateMockCode(request.code());

        Optional<User> existing = findUserByPhone(request.phone());
        if (existing.isPresent()) {
            User user = existing.get();
            if (user.role() != request.role()) {
                throw new IllegalArgumentException("该手机号已注册其他身份");
            }
            return createSession(user);
        }

        long userId = store.nextId();
        User user = new User(userId, request.role(), request.phone(), null, Instant.now());
        store.saveUser(user);
        return createSession(user);
    }

    public Map<String, Object> login(AuthLoginRequest request) {
        validateMockCode(request.code());

        User user = findUserByPhone(request.phone())
                .orElseThrow(() -> new IllegalArgumentException("账号不存在，请先注册"));
        return createSession(user);
    }

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

    public Map<String, Object> bindRole(long userId, BindRoleRequest request) {
        if (request.role() == UserRole.UNBOUND) {
            throw new IllegalArgumentException("请选择商家或达人身份");
        }

        User current = store.findUser(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        if (current.role() != UserRole.UNBOUND && current.role() != request.role()) {
            throw new IllegalArgumentException("该账号已绑定其他身份");
        }

        User updated = current.withRole(request.role());
        store.saveUser(updated);
        return createSession(updated);
    }

    public Product createProduct(ProductRequest request) {
        Product product = new Product(
                store.nextId(),
                request.merchantId(),
                request.name(),
                request.type(),
                defaultText(request.targetCategories(), request.type()),
                request.description(),
                request.goal(),
                request.budgetMin(),
                request.budgetMax(),
                request.maxQuotePerInfluencer(),
                request.platform(),
                defaultText(request.contentForms(), request.cooperationType()),
                request.fansMin(),
                request.fansMax(),
                defaultText(request.cooperationType(), "种草"),
                "ACTIVE",
                Instant.now()
        );
        store.saveProduct(product);
        return product;
    }

    public Optional<Product> updateProduct(long id, ProductRequest request) {
        Optional<Product> currentValue = store.findProduct(id);
        if (currentValue.isEmpty() || currentValue.get().merchantId() != request.merchantId()) {
            return Optional.empty();
        }
        Product current = currentValue.get();

        Product updated = new Product(
                current.id(),
                current.merchantId(),
                request.name(),
                request.type(),
                defaultText(request.targetCategories(), request.type()),
                request.description(),
                request.goal(),
                request.budgetMin(),
                request.budgetMax(),
                request.maxQuotePerInfluencer(),
                request.platform(),
                defaultText(request.contentForms(), request.cooperationType()),
                request.fansMin(),
                request.fansMax(),
                defaultText(request.cooperationType(), current.cooperationType()),
                current.status(),
                current.createdAt()
        );
        store.saveProduct(updated);
        return Optional.of(updated);
    }

    public Optional<Product> updateProductStatus(long id, ProductStatusRequest request) {
        Optional<Product> currentValue = store.findProduct(id);
        if (currentValue.isEmpty() || currentValue.get().merchantId() != request.merchantId()) {
            return Optional.empty();
        }
        Product current = currentValue.get();

        Product updated = new Product(
                current.id(),
                current.merchantId(),
                current.name(),
                current.type(),
                current.targetCategories(),
                current.description(),
                current.goal(),
                current.budgetMin(),
                current.budgetMax(),
                current.maxQuotePerInfluencer(),
                current.platform(),
                current.contentForms(),
                current.fansMin(),
                current.fansMax(),
                current.cooperationType(),
                request.status(),
                current.createdAt()
        );
        store.saveProduct(updated);
        return Optional.of(updated);
    }

    public Merchant saveMerchantProfile(MerchantRequest request) {
        Optional<Merchant> current = store.findMerchantByUserId(request.userId());

        Merchant merchant = new Merchant(
                current.map(Merchant::id).orElseGet(store::nextId),
                request.userId(),
                request.name(),
                request.industry(),
                request.description(),
                request.contact()
        );
        store.saveMerchant(merchant);
        return merchant;
    }

    public Optional<Merchant> findMerchantProfile(long userId) {
        return store.findMerchantByUserId(userId);
    }

    public List<Product> listProducts(Optional<String> type, Optional<String> platform, Optional<Integer> budgetMin, Optional<Integer> fansMin, Optional<String> cooperationType) {
        return store.listProducts().stream()
                .filter(product -> "ACTIVE".equals(product.status()))
                .filter(product -> type.map(value -> product.type().contains(value)).orElse(true))
                .filter(product -> platform.map(value -> product.platform().contains(value)).orElse(true))
                .filter(product -> budgetMin.map(value -> product.budgetMax() >= value).orElse(true))
                .filter(product -> fansMin.map(value -> product.fansMax() >= value).orElse(true))
                .filter(product -> cooperationType.map(value -> product.cooperationType().contains(value)).orElse(true))
                .sorted(Comparator.comparing(Product::createdAt).reversed())
                .toList();
    }

    public Optional<Product> findProduct(long id) {
        return store.findProduct(id);
    }

    public List<Product> listMerchantProducts(long merchantId) {
        return store.listProducts().stream()
                .filter(product -> product.merchantId() == merchantId)
                .sorted(Comparator.comparing(Product::createdAt).reversed())
                .toList();
    }

    public Map<String, Object> saveInfluencer(InfluencerRequest request) {
        Influencer influencer = new Influencer(
                request.id() == null ? store.nextId() : request.id(),
                request.userId(),
                request.nickname(),
                request.avatarUrl(),
                request.city(),
                request.platform(),
                request.fansRange(),
                request.fansCount(),
                request.category(),
                defaultText(request.categories(), request.category()),
                request.styleTags(),
                request.contentForms(),
                request.priceRange(),
                request.priceImageText(),
                request.priceVideo(),
                request.priceDetail(),
                defaultText(request.contactWechat(), request.contact()),
                request.contactPhone(),
                request.contact(),
                request.socialAccount(),
                request.isPublic()
        );
        store.saveInfluencer(influencer);
        store.saveInfluencerPortfolios(influencer.id(), toPortfolios(influencer.id(), request.portfolio()));
        cacheService.evict(PUBLIC_INFLUENCER_CACHE_KEY);
        return profileInfluencer(influencer);
    }

    public Optional<Map<String, Object>> findInfluencerProfile(long userId) {
        return store.findInfluencerByUserId(userId).map(this::profileInfluencer);
    }

    public Optional<Influencer> updatePublic(long id, boolean isPublic) {
        Optional<Influencer> currentValue = store.findInfluencer(id);
        if (currentValue.isEmpty()) {
            return Optional.empty();
        }
        Influencer current = currentValue.get();
        Influencer updated = current.withPublic(isPublic);
        store.saveInfluencer(updated);
        cacheService.evict(PUBLIC_INFLUENCER_CACHE_KEY);
        return Optional.of(updated);
    }

    public List<Map<String, Object>> listPublicInfluencers(Optional<String> category, Optional<String> platform, Optional<Integer> fansMin, Optional<String> priceRange) {
        List<Influencer> base = cacheService.get(PUBLIC_INFLUENCER_CACHE_KEY, List.class)
                .map(value -> (List<Influencer>) value)
                .orElseGet(() -> {
                    List<Influencer> visible = store.listInfluencers().stream().filter(Influencer::isPublic).toList();
                    cacheService.put(PUBLIC_INFLUENCER_CACHE_KEY, visible);
                    return visible;
                });

        return base.stream()
                .filter(item -> category.map(value -> item.category().contains(value) || defaultText(item.categories(), "").contains(value)).orElse(true))
                .filter(item -> platform.map(value -> item.platform().contains(value)).orElse(true))
                .filter(item -> fansMin.map(value -> item.fansCount() >= value).orElse(true))
                .filter(item -> priceRange.map(value -> item.priceRange().contains(value)).orElse(true))
                .map(item -> maskInfluencer(item, false))
                .toList();
    }

    public Optional<Map<String, Object>> findInfluencer(long id, long merchantId, long productId) {
        Optional<Influencer> influencerValue = store.findInfluencer(id);
        if (influencerValue.isEmpty() || !influencerValue.get().isPublic()) {
            return Optional.empty();
        }
        Influencer influencer = influencerValue.get();
        return Optional.of(maskInfluencer(influencer, hasUnlocked(merchantId, id, productId)));
    }

    public List<Map<String, Object>> matchInfluencers(long productId, int limit) {
        Optional<Product> productValue = store.findProduct(productId);
        if (productValue.isEmpty()) {
            return List.of();
        }
        Product product = productValue.get();

        return store.listInfluencers().stream()
                .filter(Influencer::isPublic)
                .map(influencer -> Map.of(
                        "score", score(product, influencer),
                        "influencer", maskInfluencer(influencer, false)
                ))
                .sorted((left, right) -> Double.compare((Double) right.get("score"), (Double) left.get("score")))
                .limit(limit)
                .toList();
    }

    public UnlockRecord unlock(UnlockRequest request) {
        Optional<UnlockRecord> current = store.findUnlock(request.merchantId(), request.influencerId(), request.productId());
        if (current.isPresent()) {
            return current.get();
        }

        Optional<Merchant> merchant = store.findMerchant(request.merchantId());
        Optional<Product> product = store.findProduct(request.productId());
        Optional<Influencer> influencer = store.findInfluencer(request.influencerId());

        if (merchant.isEmpty()) {
            throw new IllegalArgumentException("商家不存在");
        }
        if (product.isEmpty() || product.get().merchantId() != request.merchantId()) {
            throw new IllegalArgumentException("产品不属于当前商家");
        }
        if (influencer.isEmpty() || !influencer.get().isPublic()) {
            throw new IllegalArgumentException("达人未公开，无法解锁");
        }

        PaymentResult order = paymentService.createUnlockOrder(request.merchantId(), request.influencerId(), request.productId());
        // MVP 阶段 mock 支付会立即确认；接微信支付后将 confirm 调用移动到支付回调入口。
        PaymentResult result = paymentService.confirmUnlockPayment(order);
        UnlockRecord record = new UnlockRecord(store.nextId(), request.merchantId(), request.influencerId(), request.productId(), result.amountCent(), result.status(), Instant.now());
        store.saveUnlock(record);
        return record;
    }

    private Map<String, Object> maskInfluencer(Influencer influencer, boolean unlocked) {
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
        value.put("unlocked", unlocked);
        value.put("contact", unlocked ? influencer.contact() : "付费解锁后可见");
        value.put("socialAccount", unlocked ? influencer.socialAccount() : "付费解锁后可见");
        value.put("contactWechat", unlocked ? influencer.contactWechat() : "付费解锁后可见");
        value.put("contactPhone", unlocked ? influencer.contactPhone() : "付费解锁后可见");
        value.put("priceDetail", unlocked ? influencer.priceDetail() : "付费解锁后可见");
        value.put("fansCount", unlocked ? influencer.fansCount() : null);
        value.put("portfolio", store.listInfluencerPortfolios(influencer.id()));
        return value;
    }

    private Map<String, Object> profileInfluencer(Influencer influencer) {
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

    private boolean hasUnlocked(long merchantId, long influencerId, long productId) {
        return store.findUnlock(merchantId, influencerId, productId).isPresent();
    }

    private double score(Product product, Influencer influencer) {
        double category = product.type().equals(influencer.category()) ? 0.4 : 0;
        double fans = influencer.fansCount() >= product.fansMin() && influencer.fansCount() <= product.fansMax() ? 0.3 : 0;
        double budget = StringUtils.hasText(influencer.priceRange()) ? 0.2 : 0;
        double platform = product.platform().contains(influencer.platform()) || influencer.platform().contains(product.platform()) ? 0.1 : 0;
        return category + fans + budget + platform;
    }

    private String defaultText(String value, String fallback) {
        return StringUtils.hasText(value) ? value : fallback;
    }

    private void validateMockCode(String code) {
        // MVP 阶段验证码由环境变量配置，后续接入短信服务时只替换这里的校验来源。
        if (!properties.getAuth().getMockCode().equals(code)) {
            throw new IllegalArgumentException("验证码不正确");
        }
    }

    private Optional<User> findUserByPhone(String phone) {
        return store.findUserByPhone(phone);
    }

    private Map<String, Object> createSession(User user) {
        Map<String, Object> session = new LinkedHashMap<>();
        session.put("token", authTokenService.createToken(user));
        session.put("needBindRole", user.role() == UserRole.UNBOUND);
        session.put("user", user);
        if (user.role() == UserRole.MERCHANT) {
            session.put("profile", ensureMerchantProfile(user));
        } else if (user.role() == UserRole.INFLUENCER) {
            session.put("profile", maskInfluencer(ensureInfluencerProfile(user), false));
        } else {
            session.put("profile", null);
        }
        return session;
    }

    private Merchant ensureMerchantProfile(User user) {
        return store.findMerchantByUserId(user.id())
                .orElseGet(() -> {
                    Merchant merchant = new Merchant(store.nextId(), user.id(), "新商家", "待完善", "请完善品牌简介", "");
                    store.saveMerchant(merchant);
                    return merchant;
                });
    }

    private Influencer ensureInfluencerProfile(User user) {
        return store.findInfluencerByUserId(user.id())
                .orElseGet(() -> {
                    Influencer influencer = new Influencer(store.nextId(), user.id(), "新达人", "待完善", "待完善", 0, "待完善", "待完善", "", "", false);
                    store.saveInfluencer(influencer);
                    cacheService.evict(PUBLIC_INFLUENCER_CACHE_KEY);
                    return influencer;
                });
    }

    private void seed() {
        Merchant merchant = new Merchant(1, 1, "星芒美妆", "美妆", "专注新品种草和内容投放", "brand@example.com");
        store.saveMerchant(merchant);
        store.saveProduct(new Product(1, 1, "修护面膜推广", "美妆", "新品修护面膜，适合小红书和抖音种草。", "曝光", 800, 3000, "小红书", 10000, 120000, "种草", "ACTIVE", Instant.now()));
        store.saveProduct(new Product(2, 1, "咖啡店探店合作", "本地生活", "周末探店短视频合作，强调到店转化。", "引流", 300, 1500, "抖音", 5000, 80000, "探店", "ACTIVE", Instant.now()));
        store.saveProduct(new Product(3, 1, "数码新品开箱", "数码", "新品耳机开箱测评合作，适合 B站 或抖音视频。", "转化", 1000, 5000, "B站", 10000, 150000, "测评", "ACTIVE", Instant.now()));
        store.saveInfluencer(new Influencer(1, 2, "小鹿测评", "小红书", "1w-5w", 35000, "美妆", "800-1500", "lulu@example.com", "@lulu", true));
        store.saveInfluencer(new Influencer(2, 3, "阿辰探店", "抖音", "5w-10w", 76000, "本地生活", "1000-2500", "achen@example.com", "@achen", true));
        store.saveInfluencer(new Influencer(3, 4, "暂不公开达人", "B站", "1w-5w", 28000, "数码", "600-1200", "hidden@example.com", "@hidden", false));
        store.saveInfluencer(new Influencer(4, 5, "南风开箱", "B站", "5w-10w", 92000, "数码", "1500-3500", "nanfeng@example.com", "@nanfeng", true));
    }

    private List<InfluencerPortfolio> toPortfolios(long influencerId, List<PortfolioRequest> requests) {
        if (requests == null) return List.of();
        return requests.stream()
                .filter(item -> StringUtils.hasText(item.title()))
                .limit(6)
                .map(item -> new InfluencerPortfolio(item.id() == null || item.id() == 0 ? store.nextId() : item.id(), influencerId, item.title(), item.coverUrl(), item.contentUrl(), item.platform(), item.sortOrder(), Instant.now()))
                .toList();
    }

    public record ProductRequest(long merchantId, String name, String type, String targetCategories, String description, String goal, int budgetMin, int budgetMax, int maxQuotePerInfluencer, String platform, String contentForms, int fansMin, int fansMax, String cooperationType) {
    }

    public record ProductStatusRequest(long merchantId, @NotBlank String status) {
    }

    public record MerchantRequest(long userId, @NotBlank String name, @NotBlank String industry, String description, String contact) {
    }

    public record InfluencerRequest(Long id, long userId, String nickname, String avatarUrl, String city, String platform, String fansRange, int fansCount, String category, String categories, String styleTags, String contentForms, String priceRange, int priceImageText, int priceVideo, String priceDetail, String contactWechat, String contactPhone, String contact, String socialAccount, boolean isPublic, List<PortfolioRequest> portfolio) {
    }

    public record PortfolioRequest(Long id, String title, String coverUrl, String contentUrl, String platform, int sortOrder) {
    }

    public record UnlockRequest(long merchantId, long influencerId, long productId) {
    }

    public record AuthRegisterRequest(@NotBlank String phone, @NotBlank String code, @NotNull UserRole role) {
    }

    public record AuthLoginRequest(@NotBlank String phone, @NotBlank String code) {
    }

    public record WxLoginRequest(@NotBlank String code) {
    }

    public record BindRoleRequest(@NotNull UserRole role) {
    }
}
