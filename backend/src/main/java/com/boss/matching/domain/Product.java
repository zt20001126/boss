package com.boss.matching.domain;

import java.time.Instant;

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
    public Product(long id, long merchantId, String name, String type, String description, String goal, int budgetMin, int budgetMax, String platform, int fansMin, int fansMax, String cooperationType, String status, Instant createdAt) {
        this(id, merchantId, name, type, type, description, goal, budgetMin, budgetMax, 0, platform, cooperationType, fansMin, fansMax, cooperationType, status, createdAt);
    }
}
