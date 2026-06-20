package com.boss.matching.service;

import com.boss.matching.domain.Influencer;
import com.boss.matching.domain.InfluencerPortfolio;
import com.boss.matching.infra.cache.CacheService;
import com.boss.matching.persistence.MarketplaceStore;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Handles influencer profile persistence, public listing filters, and detail lookup.
 */
@Service
public class InfluencerService {
    private static final String PUBLIC_INFLUENCER_CACHE_KEY = "influencer:public:list";

    private final MarketplaceStore store;
    private final CacheService cacheService;
    private final InfluencerProfileViewService profileViewService;

    /**
     * Creates an influencer service.
     *
     * @param store marketplace persistence abstraction
     * @param cacheService cache adapter for public influencer lists
     * @param profileViewService response mapper for public influencer views
     */
    public InfluencerService(MarketplaceStore store, CacheService cacheService, InfluencerProfileViewService profileViewService) {
        this.store = store;
        this.cacheService = cacheService;
        this.profileViewService = profileViewService;
    }

    /**
     * Creates or updates an influencer profile and portfolio entries.
     *
     * @param request influencer profile request
     * @return saved profile map including portfolio
     */
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

    /**
     * Finds an influencer profile by user id.
     *
     * @param userId user id
     * @return profile map when present
     */
    public Optional<Map<String, Object>> findInfluencerProfile(long userId) {
        return store.findInfluencerByUserId(userId).map(this::profileInfluencer);
    }

    /**
     * Updates influencer public visibility.
     *
     * @param id influencer id
     * @param isPublic whether the profile should be listed publicly
     * @return updated influencer or empty when missing
     */
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

    /**
     * Lists public influencers after applying optional marketplace filters.
     *
     * @param category optional category filter
     * @param platform optional platform filter
     * @param fansMin optional minimum follower filter
     * @param priceRange optional price range filter
     * @return masked public influencer maps
     */
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
                .map(item -> profileViewService.maskInfluencer(item, false))
                .toList();
    }

    /**
     * Finds a public influencer and masks paid fields unless the merchant/product pair has unlocked them.
     *
     * @param id influencer id
     * @param merchantId merchant id
     * @param productId product id
     * @return masked or unlocked influencer map when available
     */
    public Optional<Map<String, Object>> findInfluencer(long id, long merchantId, long productId) {
        Optional<Influencer> influencerValue = store.findInfluencer(id);
        if (influencerValue.isEmpty() || !influencerValue.get().isPublic()) {
            return Optional.empty();
        }
        Influencer influencer = influencerValue.get();
        return Optional.of(profileViewService.maskInfluencer(influencer, hasUnlocked(merchantId, id, productId)));
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

    private String defaultText(String value, String fallback) {
        return StringUtils.hasText(value) ? value : fallback;
    }

    private List<InfluencerPortfolio> toPortfolios(long influencerId, List<PortfolioRequest> requests) {
        if (requests == null) return List.of();
        return requests.stream()
                .filter(item -> StringUtils.hasText(item.title()))
                .limit(6)
                .map(item -> new InfluencerPortfolio(item.id() == null || item.id() == 0 ? store.nextId() : item.id(), influencerId, item.title(), item.coverUrl(), item.contentUrl(), item.platform(), item.sortOrder(), Instant.now()))
                .toList();
    }

    /**
     * Influencer profile payload.
     *
     * @param id existing influencer id, or null for a new profile
     * @param userId owning user id
     * @param nickname display nickname
     * @param avatarUrl avatar URL
     * @param city city name
     * @param platform primary platform
     * @param fansRange display follower range
     * @param fansCount exact follower count
     * @param category primary category
     * @param categories comma-separated category list
     * @param styleTags comma-separated style tags
     * @param contentForms content form list
     * @param priceRange display price range
     * @param priceImageText image-text price
     * @param priceVideo video price
     * @param priceDetail detailed paid price information
     * @param contactWechat WeChat contact
     * @param contactPhone phone contact
     * @param contact general contact
     * @param socialAccount social account
     * @param isPublic whether the profile is publicly listed
     * @param portfolio portfolio entries
     */
    public record InfluencerRequest(Long id, long userId, String nickname, String avatarUrl, String city, String platform, String fansRange, int fansCount, String category, String categories, String styleTags, String contentForms, String priceRange, int priceImageText, int priceVideo, String priceDetail, String contactWechat, String contactPhone, String contact, String socialAccount, boolean isPublic, List<PortfolioRequest> portfolio) {
    }

    /**
     * Influencer portfolio entry payload.
     *
     * @param id existing portfolio id
     * @param title work title
     * @param coverUrl cover URL
     * @param contentUrl content URL
     * @param platform platform name
     * @param sortOrder display order
     */
    public record PortfolioRequest(Long id, String title, String coverUrl, String contentUrl, String platform, int sortOrder) {
    }
}
