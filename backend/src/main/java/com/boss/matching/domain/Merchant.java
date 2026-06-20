package com.boss.matching.domain;

/**
 * Merchant profile shown to influencers and used as the owner for product demands.
 *
 * @param id merchant id
 * @param userId owning user id
 * @param name merchant or brand name
 * @param industry merchant industry
 * @param description merchant description
 * @param contact merchant contact value
 */
public record Merchant(long id, long userId, String name, String industry, String description, String contact) {
}
