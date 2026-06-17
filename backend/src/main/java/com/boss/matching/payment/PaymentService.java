package com.boss.matching.payment;

public interface PaymentService {
    PaymentResult createUnlockOrder(long merchantId, long influencerId, long productId);

    PaymentResult confirmUnlockPayment(PaymentResult order);
}
