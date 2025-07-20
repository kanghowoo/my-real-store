package com.myrealstore.membercoupon.service.response;

import com.myrealstore.membercoupon.domain.MemberCoupon;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UseCouponResponse {
    private final Long memberCouponId;
    private final int discountAmount;
    private final int finalAmount;
    private final String description;

    @Builder
    public UseCouponResponse(Long memberCouponId, int discountAmount, int finalAmount, String description) {
        this.memberCouponId = memberCouponId;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.description = description;
    }

}
