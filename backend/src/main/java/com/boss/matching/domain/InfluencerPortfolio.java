package com.boss.matching.domain;

import java.time.Instant;

/**
 * Representative influencer work displayed on profile and detail responses.
 *
 * @param id portfolio item id
 * @param influencerId owning influencer id
 * @param title work title
 * @param coverUrl cover image URL
 * @param contentUrl content URL
 * @param platform platform where the work is published
 * @param sortOrder display order
 * @param createdTime creation timestamp
 */
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
