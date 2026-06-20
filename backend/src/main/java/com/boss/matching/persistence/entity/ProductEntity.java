package com.boss.matching.persistence.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * Persistence entity mapping merchant product and campaign records.
 */
@TableName("product")
public class ProductEntity {
    private Long id;
    private Long merchantId;
    private String name;
    private String type;
    private String targetCategories;
    private String description;
    private String goal;
    private Integer budgetMin;
    private Integer budgetMax;
    private Integer maxQuotePerInfluencer;
    private String platform;
    private String contentForms;
    private Integer fansMin;
    private Integer fansMax;
    private String cooperationType;
    private String status;
    private LocalDateTime createdAt;
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
     * Returns the merchant id value.
     * @return result value
     */
    public Long getMerchantId() {
        return merchantId;
    }

    /**
     * Sets the merchant id value.
     * @param merchantId input value
     */
    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    /**
     * Returns the name value.
     * @return result value
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name value.
     * @param name input value
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the type value.
     * @return result value
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type value.
     * @param type input value
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns the target categories value.
     * @return result value
     */
    public String getTargetCategories() {
        return targetCategories;
    }

    /**
     * Sets the target categories value.
     * @param targetCategories input value
     */
    public void setTargetCategories(String targetCategories) {
        this.targetCategories = targetCategories;
    }

    /**
     * Returns the description value.
     * @return result value
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description value.
     * @param description input value
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the goal value.
     * @return result value
     */
    public String getGoal() {
        return goal;
    }

    /**
     * Sets the goal value.
     * @param goal input value
     */
    public void setGoal(String goal) {
        this.goal = goal;
    }

    /**
     * Returns the budget min value.
     * @return result value
     */
    public Integer getBudgetMin() {
        return budgetMin;
    }

    /**
     * Sets the budget min value.
     * @param budgetMin input value
     */
    public void setBudgetMin(Integer budgetMin) {
        this.budgetMin = budgetMin;
    }

    /**
     * Returns the budget max value.
     * @return result value
     */
    public Integer getBudgetMax() {
        return budgetMax;
    }

    /**
     * Sets the budget max value.
     * @param budgetMax input value
     */
    public void setBudgetMax(Integer budgetMax) {
        this.budgetMax = budgetMax;
    }

    /**
     * Returns the max quote per influencer value.
     * @return result value
     */
    public Integer getMaxQuotePerInfluencer() {
        return maxQuotePerInfluencer;
    }

    /**
     * Sets the max quote per influencer value.
     * @param maxQuotePerInfluencer input value
     */
    public void setMaxQuotePerInfluencer(Integer maxQuotePerInfluencer) {
        this.maxQuotePerInfluencer = maxQuotePerInfluencer;
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
     * Returns the fans min value.
     * @return result value
     */
    public Integer getFansMin() {
        return fansMin;
    }

    /**
     * Sets the fans min value.
     * @param fansMin input value
     */
    public void setFansMin(Integer fansMin) {
        this.fansMin = fansMin;
    }

    /**
     * Returns the fans max value.
     * @return result value
     */
    public Integer getFansMax() {
        return fansMax;
    }

    /**
     * Sets the fans max value.
     * @param fansMax input value
     */
    public void setFansMax(Integer fansMax) {
        this.fansMax = fansMax;
    }

    /**
     * Returns the cooperation type value.
     * @return result value
     */
    public String getCooperationType() {
        return cooperationType;
    }

    /**
     * Sets the cooperation type value.
     * @param cooperationType input value
     */
    public void setCooperationType(String cooperationType) {
        this.cooperationType = cooperationType;
    }

    /**
     * Returns the status value.
     * @return result value
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status value.
     * @param status input value
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Returns the created at value.
     * @return result value
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the created at value.
     * @param createdAt input value
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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
