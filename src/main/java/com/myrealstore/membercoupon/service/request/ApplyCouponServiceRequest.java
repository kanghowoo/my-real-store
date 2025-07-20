package com.myrealstore.membercoupon.service.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ApplyCouponServiceRequest {
    private final Long memberId;
    private final Long memberCouponId;
    private final Integer originalAmount;

    @Builder
    public ApplyCouponServiceRequest(Long memberId, Long memberCouponId, Integer originalAmount) {
        this.memberId = memberId;
        this.memberCouponId = memberCouponId;
        this.originalAmount = originalAmount;
    }
}
