package com.myrealstore.payment.service.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PaymentApprovalResponse {
    private final String paymentKey;
    private final String orderId;
    private final int totalAmount;
    private final String status;

    @Builder
    public PaymentApprovalResponse(String paymentKey, String orderId, int totalAmount, String status) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public boolean isPaymentDisApproved() {
        return !"DONE".equalsIgnoreCase(status);
    }
}
