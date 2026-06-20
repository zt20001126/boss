package com.boss.matching.domain;

import java.time.Instant;

/**
 * Paid unlock record connecting a merchant, influencer, and product.
 *
 * @param id unlock record id
 * @param merchantId merchant id
 * @param influencerId influencer id
 * @param productId product id
 * @param amountCent paid amount in cents
 * @param status payment status
 * @param createdAt creation timestamp
 */
public record UnlockRecord(long id, long merchantId, long influencerId, long productId, int amountCent, String status, Instant createdAt) {
}
