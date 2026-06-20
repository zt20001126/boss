package com.boss.matching.persistence.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * Persistence entity mapping the influencer profile table.
 */
@TableName("influencer")
public class InfluencerEntity {
    private Long id;
    private Long userId;
    private String nickname;
    private String avatarUrl;
    private String city;
    private String platform;
    private String fansRange;
    private Integer fansCount;
    private String category;
    private String categories;
    private String styleTags;
    private String contentForms;
    private String priceRange;
    private Integer priceImageText;
    private Integer priceVideo;
    private String priceDetail;
    private String contactWechat;
    private String contactPhone;
    private String contact;
    private String socialAccount;
    private Boolean isPublic;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    /**
     * Returns the id value.
     * @return result value
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the id value.
     * @param id input value
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the user id value.
     * @return result value
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * Sets the user id value.
     * @param userId input value
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * Returns the nickname value.
     * @return result value
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Sets the nickname value.
     * @param nickname input value
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Returns the avatar url value.
     * @return result value
     */
    public String getAvatarUrl() {
        return avatarUrl;
    }

    /**
     * Sets the avatar url value.
     * @param avatarUrl input value
     */
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    /**
     * Returns the city value.
     * @return result value
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the city value.
     * @param city input value
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Returns the platform value.
     * @return result value
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * Sets the platform value.
     * @param platform input value
     */
    public void setPlatform(String platform) {
        this.platform = platform;
    }

    /**
     * Returns the fans range value.
     * @return result value
     */
    public String getFansRange() {
        return fansRange;
    }

    /**
     * Sets the fans range value.
     * @param fansRange input value
     */
    public void setFansRange(String fansRange) {
        this.fansRange = fansRange;
    }

    /**
     * Returns the fans count value.
     * @return result value
     */
    public Integer getFansCount() {
        return fansCount;
    }

    /**
     * Sets the fans count value.
     * @param fansCount input value
     */
    public void setFansCount(Integer fansCount) {
        this.fansCount = fansCount;
    }

    /**
     * Returns the category value.
     * @return result value
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the category value.
     * @param category input value
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Returns the categories value.
     * @return result value
     */
    public String getCategories() {
        return categories;
    }

    /**
     * Sets the categories value.
     * @param categories input value
     */
    public void setCategories(String categories) {
        this.categories = categories;
    }

    /**
     * Returns the style tags value.
     * @return result value
     */
    public String getStyleTags() {
        return styleTags;
    }

    /**
     * Sets the style tags value.
     * @param styleTags input value
     */
    public void setStyleTags(String styleTags) {
        this.styleTags = styleTags;
    }

    /**
     * Returns the content forms value.
     * @return result value
     */
    public String getContentForms() {
        return contentForms;
    }

    /**
     * Sets the content forms value.
     * @param contentForms input value
     */
    public void setContentForms(String contentForms) {
        this.contentForms = contentForms;
    }

    /**
     * Returns the price range value.
     * @return result value
     */
    public String getPriceRange() {
        return priceRange;
    }

    /**
     * Sets the price range value.
     * @param priceRange input value
     */
    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }

    /**
     * Returns the price image text value.
     * @return result value
     */
    public Integer getPriceImageText() {
        return priceImageText;
    }

    /**
     * Sets the price image text value.
     * @param priceImageText input value
     */
    public void setPriceImageText(Integer priceImageText) {
        this.priceImageText = priceImageText;
    }

    /**
     * Returns the price video value.
     * @return result value
     */
    public Integer getPriceVideo() {
        return priceVideo;
    }

    /**
     * Sets the price video value.
     * @param priceVideo input value
     */
    public void setPriceVideo(Integer priceVideo) {
        this.priceVideo = priceVideo;
    }

    /**
     * Returns the price detail value.
     * @return result value
     */
    public String getPriceDetail() {
        return priceDetail;
    }

    /**
     * Sets the price detail value.
     * @param priceDetail input value
     */
    public void setPriceDetail(String priceDetail) {
        this.priceDetail = priceDetail;
    }

    /**
     * Returns the contact wechat value.
     * @return result value
     */
    public String getContactWechat() {
        return contactWechat;
    }

    /**
     * Sets the contact wechat value.
     * @param contactWechat input value
     */
    public void setContactWechat(String contactWechat) {
        this.contactWechat = contactWechat;
    }

    /**
     * Returns the contact phone value.
     * @return result value
     */
    public String getContactPhone() {
        return contactPhone;
    }

    /**
     * Sets the contact phone value.
     * @param contactPhone input value
     */
    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    /**
     * Returns the contact value.
     * @return result value
     */
    public String getContact() {
        return contact;
    }

    /**
     * Sets the contact value.
     * @param contact input value
     */
    public void setContact(String contact) {
        this.contact = contact;
    }

    /**
     * Returns the social account value.
     * @return result value
     */
    public String getSocialAccount() {
        return socialAccount;
    }

    /**
     * Sets the social account value.
     * @param socialAccount input value
     */
    public void setSocialAccount(String socialAccount) {
        this.socialAccount = socialAccount;
    }

    /**
     * Returns the is public value.
     * @return result value
     */
    public Boolean getIsPublic() {
        return isPublic;
    }

    /**
     * Sets the is public value.
     * @param isPublic input value
     */
    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    /**
     * Returns the created time value.
     * @return result value
     */
    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    /**
     * Sets the created time value.
     * @param createdTime input value
     */
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    /**
     * Returns the updated time value.
     * @return result value
     */
    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    /**
     * Sets the updated time value.
     * @param updatedTime input value
     */
    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }
}
