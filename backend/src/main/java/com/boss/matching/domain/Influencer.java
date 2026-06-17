package com.boss.matching.domain;

public record Influencer(
        long id,
        long userId,
        String nickname,
        String avatarUrl,
        String city,
        String platform,
        String fansRange,
        int fansCount,
        String category,
        String categories,
        String styleTags,
        String contentForms,
        String priceRange,
        int priceImageText,
        int priceVideo,
        String priceDetail,
        String contactWechat,
        String contactPhone,
        String contact,
        String socialAccount,
        boolean isPublic
) {
    public Influencer(long id, long userId, String nickname, String platform, String fansRange, int fansCount, String category, String priceRange, String contact, String socialAccount, boolean isPublic) {
        this(id, userId, nickname, "", "", platform, fansRange, fansCount, category, category, "", "", priceRange, 0, 0, "", contact, "", contact, socialAccount, isPublic);
    }

    public Influencer withPublic(boolean nextPublic) {
        return new Influencer(id, userId, nickname, avatarUrl, city, platform, fansRange, fansCount, category, categories, styleTags, contentForms, priceRange, priceImageText, priceVideo, priceDetail, contactWechat, contactPhone, contact, socialAccount, nextPublic);
    }
}
