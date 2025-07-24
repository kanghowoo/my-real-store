package com.myrealstore.payment.service.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PaymentConfirmServiceRequest {

    private final Long memberId;
    private final String paymentKey;
    private final String orderId;
    private final int amount;
    private final String provider;
    private final Long memberCouponId;

    @Builder
    public PaymentConfirmServiceRequest(Long memberId, String paymentKey, String orderId, int amount,
                                        String provider, Long memberCouponId) {
        this.memberId = memberId;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.provider = getLowerCase(provider);
        this.memberCouponId = getMemberCouponId(memberCouponId);
    }

    public PaymentConfirmServiceRequest withAmount(int newAmount) {
        return PaymentConfirmServiceRequest.builder()
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

    private static long getMemberCouponId(Long memberCouponId) {
        return (memberCouponId != null) ? memberCouponId : 0L;
    }

    private String getLowerCase(String provider) {
        return provider.toLowerCase();
    }
}
