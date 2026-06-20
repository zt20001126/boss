package com.boss.matching.payment;

import com.boss.matching.config.AppProperties;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Mock payment implementation that returns successful unlock orders for local use.
 */
@Service
public class MockPaymentService implements PaymentService {
    private final AppProperties properties;

    /**
     * Creates a MockPaymentService instance.
     * @param properties input value
     */
    public MockPaymentService(AppProperties properties) {
        this.properties = properties;
    }

    /** {@inheritDoc} */
    @Override
    public PaymentResult createUnlockOrder(long merchantId, long influencerId, long productId) {
        return new PaymentResult("mock-pay-" + UUID.randomUUID(), "PENDING", properties.getPayment().getUnlockAmountCent());
    }

    /** {@inheritDoc} */
    @Override
    public PaymentResult confirmUnlockPayment(PaymentResult order) {
        return new PaymentResult(order.paymentId(), "PAID", order.amountCent());
    }
}
