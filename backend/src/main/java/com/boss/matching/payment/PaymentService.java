package com.boss.matching.payment;

/**
 * Defines payment operations required by the unlock flow.
 */
public interface PaymentService {
    PaymentResult createUnlockOrder(long merchantId, long influencerId, long productId);

    PaymentResult confirmUnlockPayment(PaymentResult order);
}
