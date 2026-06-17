package com.boss.matching.domain;

import java.time.Instant;

public record UnlockRecord(long id, long merchantId, long influencerId, long productId, int amountCent, String status, Instant createdAt) {
}
