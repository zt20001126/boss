package com.boss.matching.domain;

import java.time.Instant;

public record User(long id, UserRole role, String phone, String openid, String unionid, String loginType, Instant createdTime) {
    public User(long id, UserRole role, String phone, String openid, Instant createdTime) {
        this(id, role, phone, openid, null, openid == null ? "PHONE" : "WECHAT", createdTime);
    }

    public User(long id, UserRole role, String openid, Instant createdTime) {
        this(id, role, null, openid, createdTime);
    }

    public User withRole(UserRole nextRole) {
        return new User(id, nextRole, phone, openid, unionid, loginType, createdTime);
    }
}
