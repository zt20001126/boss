package com.boss.matching.persistence.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * Persistence entity mapping paid unlock records.
 */
@TableName("unlock_record")
public class UnlockRecordEntity {
    private Long id;
    private Long merchantId;
    private Long influencerId;
    private Long productId;
    private Integer amountCent;
    private String status;
    private String unlockType;
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
     * Returns the product id value.
     * @return result value
     */
    public Long getProductId() {
        return productId;
    }

    /**
     * Sets the product id value.
     * @param productId input value
     */
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    /**
     * Returns the amount cent value.
     * @return result value
     */
    public Integer getAmountCent() {
        return amountCent;
    }

    /**
     * Sets the amount cent value.
     * @param amountCent input value
     */
    public void setAmountCent(Integer amountCent) {
        this.amountCent = amountCent;
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
     * Returns the unlock type value.
     * @return result value
     */
    public String getUnlockType() {
        return unlockType;
    }

    /**
     * Sets the unlock type value.
     * @param unlockType input value
     */
    public void setUnlockType(String unlockType) {
        this.unlockType = unlockType;
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
