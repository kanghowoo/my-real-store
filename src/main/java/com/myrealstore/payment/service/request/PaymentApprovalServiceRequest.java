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
    private final Long memberCouponId;

    @Builder
    public PaymentApprovalServiceRequest(Long memberId, String paymentKey, String orderId, int amount,
                                         String provider, Long memberCouponId) {
        this.memberId = memberId;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.provider = getLowerCase(provider);
        this.memberCouponId = memberCouponId;
    }

    public PaymentApprovalServiceRequest withAmount(int newAmount) {
        return PaymentApprovalServiceRequest.builder()
                                            .memberId(this.memberId)
                                            .amount(newAmount)
                                            .provider(this.provider)
                                            .orderId(this.orderId)
                                            .paymentKey(this.paymentKey)
                                            .memberCouponId(this.memberCouponId)
                                            .build();
    }

    public PaymentCancelServiceRequest toPaymentCancelServiceRequest() {
        return PaymentCancelServiceRequest.builder()
                .paymentKey(paymentKey)
                .orderId(orderId)
                .amount(amount)
                .reason("충전 실패")
                .build();
    }

    private String getLowerCase(String provider) {
        return provider.toLowerCase();
    }
}
