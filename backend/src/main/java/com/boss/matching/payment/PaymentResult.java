package com.boss.matching.payment;

public record PaymentResult(String paymentId, String status, int amountCent) {
}
