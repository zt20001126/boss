package com.boss.matching.domain;

/**
 * Influencer profile aggregate used by listing, matching, and paid contact unlock flows.
 *
 * @param id influencer id
 * @param userId owning user id
 * @param nickname display nickname
 * @param avatarUrl avatar image URL
 * @param city city where the influencer operates
 * @param platform primary content platform
 * @param fansRange display follower range
 * @param fansCount exact follower count used for matching
 * @param category primary content category
 * @param categories comma-separated category list
 * @param styleTags comma-separated content style tags
 * @param contentForms supported content formats
 * @param priceRange display price range
 * @param priceImageText image-text quote
 * @param priceVideo video quote
 * @param priceDetail detailed paid quote text
 * @param contactWechat WeChat contact
 * @param contactPhone phone contact
 * @param contact general contact value
 * @param socialAccount social account identifier
 * @param isPublic whether the profile is visible to merchants
 */
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
    /**
     * Creates a legacy influencer profile with only the original MVP fields.
     *
     * @param id influencer id
     * @param userId owning user id
     * @param nickname display nickname
     * @param platform primary content platform
     * @param fansRange display follower range
     * @param fansCount exact follower count
     * @param category primary content category
     * @param priceRange display price range
     * @param contact general contact value
     * @param socialAccount social account identifier
     * @param isPublic whether the profile is visible to merchants
     */
    public Influencer(long id, long userId, String nickname, String platform, String fansRange, int fansCount, String category, String priceRange, String contact, String socialAccount, boolean isPublic) {
        this(id, userId, nickname, "", "", platform, fansRange, fansCount, category, category, "", "", priceRange, 0, 0, "", contact, "", contact, socialAccount, isPublic);
    }

    /**
     * Returns a copy with updated public visibility.
     *
     * @param nextPublic next public visibility flag
     * @return influencer copy with the new visibility flag
     */
    public Influencer withPublic(boolean nextPublic) {
        return new Influencer(id, userId, nickname, avatarUrl, city, platform, fansRange, fansCount, category, categories, styleTags, contentForms, priceRange, priceImageText, priceVideo, priceDetail, contactWechat, contactPhone, contact, socialAccount, nextPublic);
    }
}
