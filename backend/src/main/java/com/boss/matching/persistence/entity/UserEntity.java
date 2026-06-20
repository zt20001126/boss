package com.boss.matching.persistence.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * Persistence entity mapping mini-program user accounts.
 */
@TableName("user")
public class UserEntity {
    private Long id;
    private String role;
    private String phone;
    private String password;
    private String openid;
    private String unionid;
    private String loginType;
    private String status;
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
     * Returns the role value.
     * @return result value
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the role value.
     * @param role input value
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Returns the phone value.
     * @return result value
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the phone value.
     * @param phone input value
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Returns the password value.
     * @return result value
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password value.
     * @param password input value
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the openid value.
     * @return result value
     */
    public String getOpenid() {
        return openid;
    }

    /**
     * Sets the openid value.
     * @param openid input value
     */
    public void setOpenid(String openid) {
        this.openid = openid;
    }

    /**
     * Returns the unionid value.
     * @return result value
     */
    public String getUnionid() {
        return unionid;
    }

    /**
     * Sets the unionid value.
     * @param unionid input value
     */
    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    /**
     * Returns the login type value.
     * @return result value
     */
    public String getLoginType() {
        return loginType;
    }

    /**
     * Sets the login type value.
     * @param loginType input value
     */
    public void setLoginType(String loginType) {
        this.loginType = loginType;
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
