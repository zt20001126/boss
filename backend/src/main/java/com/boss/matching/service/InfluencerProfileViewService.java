package com.boss.matching.service;

import com.boss.matching.domain.Influencer;
import com.boss.matching.persistence.MarketplaceStore;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Builds influencer response views, including masking paid fields for locked profiles.
 */
@Service
public class InfluencerProfileViewService {
    private static final String LOCKED_TEXT = "\u4ed8\u8d39\u89e3\u9501\u540e\u53ef\u89c1";

    private final MarketplaceStore store;

    /**
     * Creates an influencer response view service.
     *
     * @param store marketplace persistence abstraction
     */
    public InfluencerProfileViewService(MarketplaceStore store) {
        this.store = store;
    }

    /**
     * Builds a marketplace-facing influencer map with paid fields hidden unless unlocked.
     *
     * @param influencer influencer domain object
     * @param unlocked whether paid fields should be visible
     * @return response map consumed by existing mini-program pages
     */
    public Map<String, Object> maskInfluencer(Influencer influencer, boolean unlocked) {
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
        value.put("contact", unlocked ? influencer.contact() : LOCKED_TEXT);
        value.put("socialAccount", unlocked ? influencer.socialAccount() : LOCKED_TEXT);
        value.put("contactWechat", unlocked ? influencer.contactWechat() : LOCKED_TEXT);
        value.put("contactPhone", unlocked ? influencer.contactPhone() : LOCKED_TEXT);
        value.put("priceDetail", unlocked ? influencer.priceDetail() : LOCKED_TEXT);
        value.put("fansCount", unlocked ? influencer.fansCount() : null);
        value.put("portfolio", store.listInfluencerPortfolios(influencer.id()));
        return value;
    }
}
