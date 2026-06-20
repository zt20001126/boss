package com.boss.matching.persistence.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * Persistence entity mapping merchant profile records.
 */
@TableName("merchant")
public class MerchantEntity {
    private Long id;
    private Long userId;
    private String name;
    private String industry;
    private String description;
    private String contact;
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
     * Returns the industry value.
     * @return result value
     */
    public String getIndustry() {
        return industry;
    }

    /**
     * Sets the industry value.
     * @param industry input value
     */
    public void setIndustry(String industry) {
        this.industry = industry;
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
