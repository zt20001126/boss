package com.boss.matching.domain;

import java.time.Instant;

/**
 * Merchant product demand used by product listing and influencer matching.
 *
 * @param id product id
 * @param merchantId owning merchant id
 * @param name product name
 * @param type industry or product type
 * @param targetCategories desired influencer categories
 * @param description product description
 * @param goal campaign goal
 * @param budgetMin minimum budget
 * @param budgetMax maximum budget
 * @param maxQuotePerInfluencer maximum acceptable quote
 * @param platform target content platform
 * @param contentForms desired content forms
 * @param fansMin minimum follower requirement
 * @param fansMax maximum follower requirement
 * @param cooperationType cooperation type
 * @param status product status
 * @param createdAt creation timestamp
 */
public record Product(
        long id,
        long merchantId,
        String name,
        String type,
        String targetCategories,
        String description,
        String goal,
        int budgetMin,
        int budgetMax,
        int maxQuotePerInfluencer,
        String platform,
        String contentForms,
        int fansMin,
        int fansMax,
        String cooperationType,
        String status,
        Instant createdAt
) {
    /**
     * Creates a legacy product demand with only the original MVP fields.
     *
     * @param id product id
     * @param merchantId owning merchant id
     * @param name product name
     * @param type industry or product type
     * @param description product description
     * @param goal campaign goal
     * @param budgetMin minimum budget
     * @param budgetMax maximum budget
     * @param platform target content platform
     * @param fansMin minimum follower requirement
     * @param fansMax maximum follower requirement
     * @param cooperationType cooperation type
     * @param status product status
     * @param createdAt creation timestamp
     */
    public Product(long id, long merchantId, String name, String type, String description, String goal, int budgetMin, int budgetMax, String platform, int fansMin, int fansMax, String cooperationType, String status, Instant createdAt) {
        this(id, merchantId, name, type, type, description, goal, budgetMin, budgetMax, 0, platform, cooperationType, fansMin, fansMax, cooperationType, status, createdAt);
    }
}
