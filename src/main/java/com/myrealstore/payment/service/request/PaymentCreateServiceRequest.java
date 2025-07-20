package com.myrealstore.payment.service.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PaymentCreateServiceRequest {
    private final long memberId;
    private final String orderId;
    private final String paymentKey;
    private final long memberCouponId;
    private final int finalAmount;
    private final Integer discountAmount;

    @Builder
    public PaymentCreateServiceRequest(long memberId, String orderId, String paymentKey, long memberCouponId,
                                       int finalAmount,
                                       Integer discountAmount) {
        this.memberId = memberId;
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.memberCouponId = memberCouponId;
        this.finalAmount = finalAmount;
        this.discountAmount = discountAmount;
    }
}
