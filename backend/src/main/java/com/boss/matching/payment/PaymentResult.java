package com.boss.matching.payment;

/**
 * Payment order result returned by payment providers.
 */
public record PaymentResult(String paymentId, String status, int amountCent) {
}
