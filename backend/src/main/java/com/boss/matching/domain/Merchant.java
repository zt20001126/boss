package com.boss.matching.domain;

public record Merchant(long id, long userId, String name, String industry, String description, String contact) {
}
