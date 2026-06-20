package com.boss.matching.domain;

/**
 * Supported account roles for authentication and role-based routing.
 */
public enum UserRole {
    /**
     * WeChat user that has not selected a business role yet.
     */
    UNBOUND,
    /**
     * Merchant user that publishes product demands.
     */
    MERCHANT,
    /**
     * Influencer user that browses merchant products.
     */
    INFLUENCER
}
