package com.boss.matching.service;

import com.boss.matching.config.AppProperties;
import com.boss.matching.domain.UserRole;
import com.boss.matching.infra.cache.MemoryCacheService;
import com.boss.matching.payment.MockPaymentService;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class MarketplaceServiceTest {
    private final AppProperties properties = new AppProperties();
    private final MarketplaceService service = new MarketplaceService(
            new MemoryCacheService(),
            new MockPaymentService(properties),
            new AuthTokenService(properties),
            properties
    );

    @Test
    void listPublicInfluencersOnlyReturnsVisibleItems() {
        var items = service.listPublicInfluencers(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

        assertThat(items).hasSize(3);
        assertThat(items).allSatisfy(item -> assertThat(item.get("isPublic")).isEqualTo(true));
    }

    @Test
    void influencerDetailMasksPaidFieldsBeforeUnlock() {
        var detail = service.findInfluencer(1, 1, 1);

        assertThat(detail).isPresent();
        assertThat(detail.get().get("contact")).isEqualTo("付费解锁后可见");
        assertThat(detail.get().get("fansCount")).isNull();
    }

    @Test
    void unlockRevealsPaidFieldsAndIsIdempotent() {
        var first = service.unlock(new MarketplaceService.UnlockRequest(1, 1, 1));
        var second = service.unlock(new MarketplaceService.UnlockRequest(1, 1, 1));
        var detail = service.findInfluencer(1, 1, 1);

        assertThat(first.id()).isEqualTo(second.id());
        assertThat(detail).isPresent();
        assertThat(detail.get().get("contact")).isEqualTo("lulu@example.com");
        assertThat(detail.get().get("fansCount")).isEqualTo(35000);
    }

    @Test
    void matchInfluencersSortsByScore() {
        var matches = service.matchInfluencers(1, 2);

        assertThat(matches).hasSize(2);
        assertThat((Double) matches.get(0).get("score")).isGreaterThanOrEqualTo((Double) matches.get(1).get("score"));
    }

    @Test
    void wxLoginCreatesUnboundUserThenBindsRole() {
        var login = service.wxLogin(new MarketplaceService.WxLoginRequest("test-code"));

        assertThat(login.get("needBindRole")).isEqualTo(true);
        var user = (com.boss.matching.domain.User) login.get("user");
        assertThat(user.role()).isEqualTo(UserRole.UNBOUND);

        var bound = service.bindRole(user.id(), new MarketplaceService.BindRoleRequest(UserRole.MERCHANT));

        assertThat(bound.get("needBindRole")).isEqualTo(false);
        assertThat(((com.boss.matching.domain.User) bound.get("user")).role()).isEqualTo(UserRole.MERCHANT);
        assertThat(bound.get("profile")).isNotNull();
    }

    @Test
    void influencerPaidFieldsStayMaskedUntilUnlock() {
        var saved = service.saveInfluencer(new MarketplaceService.InfluencerRequest(
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
                java.util.List.of(new MarketplaceService.PortfolioRequest(null, "代表作品", "", "https://example.com/work", "小红书", 1))
        ));

        long influencerId = ((Number) saved.get("id")).longValue();
        var masked = service.findInfluencer(influencerId, 1, 1).orElseThrow();
        assertThat(masked.get("contactWechat")).isEqualTo("付费解锁后可见");
        assertThat(masked.get("priceDetail")).isEqualTo("付费解锁后可见");
        assertThat(masked.get("portfolio").toString()).contains("代表作品");

        service.unlock(new MarketplaceService.UnlockRequest(1, influencerId, 1));
        var unlocked = service.findInfluencer(influencerId, 1, 1).orElseThrow();

        assertThat(unlocked.get("contactWechat")).isEqualTo("wx-demo");
        assertThat(unlocked.get("priceDetail")).isEqualTo("视频另计脚本费用");
    }
}
