package com.boss.matching.service;

import com.boss.matching.config.AppProperties;
import com.boss.matching.config.MarketplaceSeedDataInitializer;
import com.boss.matching.domain.UserRole;
import com.boss.matching.domain.Product;
import com.boss.matching.domain.ProductFavorite;
import com.boss.matching.infra.cache.MemoryCacheService;
import com.boss.matching.payment.MockPaymentService;
import com.boss.matching.persistence.MemoryMarketplaceStore;
import com.boss.matching.persistence.MarketplaceStore;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class MarketplaceServiceTest {
    private final AppProperties properties = new AppProperties();
    private final MarketplaceStore store = new MemoryMarketplaceStore();
    private final MemoryCacheService cacheService = new MemoryCacheService();
    private final InfluencerProfileViewService profileViewService = new InfluencerProfileViewService(store);
    private final UnlockService unlockService = new UnlockService(store, new MockPaymentService(properties));
    private final InfluencerService influencerService = new InfluencerService(store, cacheService, profileViewService);
    private final ProductService productService = new ProductService(store, profileViewService);
    private final ProductFavoriteService favoriteService = new ProductFavoriteService(store);

    MarketplaceServiceTest() {
        new MarketplaceSeedDataInitializer(store).seed();
    }

    @Test
    void listPublicInfluencersOnlyReturnsVisibleItems() {
        var items = influencerService.listPublicInfluencers(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

        assertThat(items).hasSize(3);
        assertThat(items).allSatisfy(item -> assertThat(item.get("isPublic")).isEqualTo(true));
    }

    @Test
    void influencerDetailMasksPaidFieldsBeforeUnlock() {
        var detail = influencerService.findInfluencer(1, 1, 1);

        assertThat(detail).isPresent();
        assertThat(detail.get().get("contact")).isEqualTo("付费解锁后可见");
        assertThat(detail.get().get("fansCount")).isNull();
    }

    @Test
    void unlockRevealsPaidFieldsAndIsIdempotent() {
        var first = unlockService.unlock(new UnlockService.UnlockRequest(1, 1, 1));
        var second = unlockService.unlock(new UnlockService.UnlockRequest(1, 1, 1));
        var detail = influencerService.findInfluencer(1, 1, 1);

        assertThat(first.id()).isEqualTo(second.id());
        assertThat(detail).isPresent();
        assertThat(detail.get().get("contact")).isEqualTo("lulu@example.com");
        assertThat(detail.get().get("fansCount")).isEqualTo(35000);
    }

    @Test
    void matchInfluencersSortsByScore() {
        var matches = productService.matchInfluencers(1, 2);

        assertThat(matches).hasSize(2);
        assertThat((Double) matches.get(0).get("score")).isGreaterThanOrEqualTo((Double) matches.get(1).get("score"));
    }

    @Test
    void productFavoriteAddAndRemoveAreIdempotent() {
        assertThat(favoriteService.isFavorite(2, 1)).isFalse();

        favoriteService.favorite(2, 1);
        favoriteService.favorite(2, 1);

        assertThat(favoriteService.isFavorite(2, 1)).isTrue();
        assertThat(store.listProductFavorites(1)).hasSize(1);

        favoriteService.unfavorite(2, 1);
        favoriteService.unfavorite(2, 1);
        assertThat(favoriteService.isFavorite(2, 1)).isFalse();
    }

    @Test
    void productFavoritesAreIsolatedByInfluencerAccount() {
        favoriteService.favorite(2, 1);

        assertThat(favoriteService.listFavorites(2)).extracting(Product::id).containsExactly(1L);
        assertThat(favoriteService.listFavorites(3)).isEmpty();
    }

    @Test
    void productFavoritesKeepOfflineProductsAndSortNewestFirst() {
        Instant now = Instant.now();
        store.saveProductFavorite(new ProductFavorite(2001, 1, 1, now.minusSeconds(10)));
        store.saveProductFavorite(new ProductFavorite(2002, 1, 2, now));

        Product active = store.findProduct(1).orElseThrow();
        store.saveProduct(new Product(active.id(), active.merchantId(), active.name(), active.type(), active.targetCategories(), active.description(), active.goal(), active.budgetMin(), active.budgetMax(), active.maxQuotePerInfluencer(), active.platform(), active.contentForms(), active.fansMin(), active.fansMax(), active.cooperationType(), "OFFLINE", active.createdAt()));

        var favorites = favoriteService.listFavorites(2);
        assertThat(favorites).extracting(Product::id).containsExactly(2L, 1L);
        assertThat(favorites.get(1).status()).isEqualTo("OFFLINE");
    }

    @Test
    void wxLoginCreatesUnboundUserThenBindsRole() {
        var authService = new AuthService(
                new MemoryMarketplaceStore(),
                new MemoryCacheService(),
                new AuthTokenService(properties),
                properties,
                new WechatAuthService(properties)
        );
        var login = authService.wxLogin(new AuthService.WxLoginRequest("test-code"));

        assertThat(login.get("needBindRole")).isEqualTo(true);
        var user = (com.boss.matching.domain.User) login.get("user");
        assertThat(user.role()).isEqualTo(UserRole.UNBOUND);

        var bound = authService.bindRole(user.id(), new AuthService.BindRoleRequest(UserRole.MERCHANT));

        assertThat(bound.get("needBindRole")).isEqualTo(false);
        assertThat(((com.boss.matching.domain.User) bound.get("user")).role()).isEqualTo(UserRole.MERCHANT);
        assertThat(bound.get("profile")).isNotNull();
    }

    @Test
    void influencerPaidFieldsStayMaskedUntilUnlock() {
        var saved = influencerService.saveInfluencer(new InfluencerService.InfluencerRequest(
                99L,
                99L,
                "结构化达人",
                "https://example.com/a.png",
                "上海",
                "小红书",
                "1w-5w",
                22000,
                "美妆",
                "美妆,护肤",
                "测评,种草",
                "图文,短视频",
                "800-1800",
                900,
                1800,
                "视频另计脚本费用",
                "wx-demo",
                "13800000000",
                "demo@example.com",
                "@demo",
                true,
                java.util.List.of(new InfluencerService.PortfolioRequest(null, "代表作品", "", "https://example.com/work", "小红书", 1))
        ));

        long influencerId = ((Number) saved.get("id")).longValue();
        var masked = influencerService.findInfluencer(influencerId, 1, 1).orElseThrow();
        assertThat(masked.get("contactWechat")).isEqualTo("付费解锁后可见");
        assertThat(masked.get("priceDetail")).isEqualTo("付费解锁后可见");
        assertThat(masked.get("portfolio").toString()).contains("代表作品");

        unlockService.unlock(new UnlockService.UnlockRequest(1, influencerId, 1));
        var unlocked = influencerService.findInfluencer(influencerId, 1, 1).orElseThrow();

        assertThat(unlocked.get("contactWechat")).isEqualTo("wx-demo");
        assertThat(unlocked.get("priceDetail")).isEqualTo("视频另计脚本费用");
    }
}
