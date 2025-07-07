package com.myrealstore.payment.service.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PaymentApprovalServiceRequest {

    private final Long memberId;
    private final String paymentKey;
    private final String orderId;
    private final int amount;
    private final String provider;

    @Builder
    public PaymentApprovalServiceRequest(Long memberId, String paymentKey, String orderId, int amount,
                                         String provider) {
        this.memberId = memberId;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.provider = getLowerCase(provider);
    }

    public PaymentCancelServiceRequest toPaymentCancelServiceRequest() {
        return PaymentCancelServiceRequest.builder()
                .paymentKey(paymentKey)
                .orderId(orderId)
                .amount(amount)
                .build();
    }

    private String getLowerCase(String provider) {
        return provider.toLowerCase();
    }
}
