package com.myrealstore.membercoupon.service.response;

import java.time.LocalDateTime;

import com.myrealstore.coupon.domain.Coupon;
import com.myrealstore.coupon.domain.DiscountType;
import com.myrealstore.membercoupon.domain.MemberCoupon;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberCouponResponse {
    private final Long memberCouponId;
    private final String name;
    private final int discountValue;
    private final DiscountType discountType;
    private final boolean used;
    private final LocalDateTime expiredAt;

    @Builder
    public MemberCouponResponse(Long memberCouponId, String name, int discountValue, DiscountType discountType,
                                boolean used, LocalDateTime expiredAt) {
        this.memberCouponId = memberCouponId;
        this.name = name;
        this.discountValue = discountValue;
        this.discountType = discountType;
        this.used = used;
        this.expiredAt = expiredAt;
    }

    public static MemberCouponResponse of(MemberCoupon memberCoupon) {
        Coupon coupon = memberCoupon.getCoupon();
        return MemberCouponResponse.builder()
                                   .memberCouponId(memberCoupon.getId())
                                   .name(coupon.getName())
                                   .discountType(coupon.getDiscountType())
                                   .discountValue(coupon.getDiscountValue())
                                   .used(memberCoupon.isUsed())
                                   .expiredAt(coupon.getExpiredAt())
                                   .build();
    }

}
