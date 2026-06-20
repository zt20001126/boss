package com.boss.matching.config;

import com.boss.matching.domain.Influencer;
import com.boss.matching.domain.Merchant;
import com.boss.matching.domain.Product;
import com.boss.matching.persistence.MarketplaceStore;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Loads temporary marketplace seed data for the current in-memory/demo environment.
 */
@Component
public class MarketplaceSeedDataInitializer {
    private final MarketplaceStore store;

    /**
     * Creates a seed data initializer.
     *
     * @param store marketplace persistence abstraction
     */
    public MarketplaceSeedDataInitializer(MarketplaceStore store) {
        this.store = store;
    }

    /**
     * Seeds demo merchants, products, and influencers.
     */
    @PostConstruct
    public void seed() {
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
}
