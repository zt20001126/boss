package com.boss.matching.payment;

import com.boss.matching.config.AppProperties;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MockPaymentService implements PaymentService {
    private final AppProperties properties;

    public MockPaymentService(AppProperties properties) {
        this.properties = properties;
    }

    @Override
    public PaymentResult createUnlockOrder(long merchantId, long influencerId, long productId) {
        return new PaymentResult("mock-pay-" + UUID.randomUUID(), "PENDING", properties.getPayment().getUnlockAmountCent());
    }

    @Override
    public PaymentResult confirmUnlockPayment(PaymentResult order) {
        return new PaymentResult(order.paymentId(), "PAID", order.amountCent());
    }
}
