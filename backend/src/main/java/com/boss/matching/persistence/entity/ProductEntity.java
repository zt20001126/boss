package com.boss.matching.persistence.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTargetCategories() {
        return targetCategories;
    }

    public void setTargetCategories(String targetCategories) {
        this.targetCategories = targetCategories;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public Integer getBudgetMin() {
        return budgetMin;
    }

    public void setBudgetMin(Integer budgetMin) {
        this.budgetMin = budgetMin;
    }

    public Integer getBudgetMax() {
        return budgetMax;
    }

    public void setBudgetMax(Integer budgetMax) {
        this.budgetMax = budgetMax;
    }

    public Integer getMaxQuotePerInfluencer() {
        return maxQuotePerInfluencer;
    }

    public void setMaxQuotePerInfluencer(Integer maxQuotePerInfluencer) {
        this.maxQuotePerInfluencer = maxQuotePerInfluencer;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getContentForms() {
        return contentForms;
    }

    public void setContentForms(String contentForms) {
        this.contentForms = contentForms;
    }

    public Integer getFansMin() {
        return fansMin;
    }

    public void setFansMin(Integer fansMin) {
        this.fansMin = fansMin;
    }

    public Integer getFansMax() {
        return fansMax;
    }

    public void setFansMax(Integer fansMax) {
        this.fansMax = fansMax;
    }

    public String getCooperationType() {
        return cooperationType;
    }

    public void setCooperationType(String cooperationType) {
        this.cooperationType = cooperationType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }
}
