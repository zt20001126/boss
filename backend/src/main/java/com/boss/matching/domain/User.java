package com.boss.matching.domain;

import java.time.Instant;

/**
 * Application user account created from phone login or WeChat login.
 *
 * @param id user id
 * @param role current account role
 * @param phone phone number for phone-code accounts
 * @param openid WeChat openid for WeChat accounts
 * @param unionid WeChat unionid when available
 * @param loginType login provider type
 * @param createdTime creation timestamp
 */
public record User(long id, UserRole role, String phone, String openid, String unionid, String loginType, Instant createdTime) {
    /**
     * Creates a user from phone/openid with inferred login type.
     *
     * @param id user id
     * @param role current account role
     * @param phone phone number
     * @param openid WeChat openid
     * @param createdTime creation timestamp
     */
    public User(long id, UserRole role, String phone, String openid, Instant createdTime) {
        this(id, role, phone, openid, null, openid == null ? "PHONE" : "WECHAT", createdTime);
    }

    /**
     * Creates a WeChat user with an openid.
     *
     * @param id user id
     * @param role current account role
     * @param openid WeChat openid
     * @param createdTime creation timestamp
     */
    public User(long id, UserRole role, String openid, Instant createdTime) {
        this(id, role, null, openid, createdTime);
    }

    /**
     * Returns a copy with a different role.
     *
     * @param nextRole next account role
     * @return user copy with the new role
     */
    public User withRole(UserRole nextRole) {
        return new User(id, nextRole, phone, openid, unionid, loginType, createdTime);
    }
}
