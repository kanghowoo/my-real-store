package com.myrealstore.payment.service.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PaymentCancelServiceRequest {
    private final String paymentKey;
    private final String orderId;
    private final int amount;
    private final String reason;

    @Builder
    public PaymentCancelServiceRequest(String paymentKey, String orderId, int amount, String reason) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.reason = reason;
    }
}
