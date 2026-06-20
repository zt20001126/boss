package com.boss.matching.persistence.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * Persistence entity mapping influencer portfolio work records.
 */
@TableName("influencer_portfolio")
public class InfluencerPortfolioEntity {
    private Long id;
    private Long influencerId;
    private String title;
    private String coverUrl;
    private String contentUrl;
    private String platform;
    private Integer sortOrder;
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
     * Returns the influencer id value.
     * @return result value
     */
    public Long getInfluencerId() {
        return influencerId;
    }

    /**
     * Sets the influencer id value.
     * @param influencerId input value
     */
    public void setInfluencerId(Long influencerId) {
        this.influencerId = influencerId;
    }

    /**
     * Returns the title value.
     * @return result value
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title value.
     * @param title input value
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the cover url value.
     * @return result value
     */
    public String getCoverUrl() {
        return coverUrl;
    }

    /**
     * Sets the cover url value.
     * @param coverUrl input value
     */
    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    /**
     * Returns the content url value.
     * @return result value
     */
    public String getContentUrl() {
        return contentUrl;
    }

    /**
     * Sets the content url value.
     * @param contentUrl input value
     */
    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
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
     * Returns the sort order value.
     * @return result value
     */
    public Integer getSortOrder() {
        return sortOrder;
    }

    /**
     * Sets the sort order value.
     * @param sortOrder input value
     */
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
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
