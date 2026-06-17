package com.boss.matching.domain;

import java.time.Instant;

public record InfluencerPortfolio(
        long id,
        long influencerId,
        String title,
        String coverUrl,
        String contentUrl,
        String platform,
        int sortOrder,
        Instant createdTime
) {
}
