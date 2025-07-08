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
    private final Long couponId;
    private final String name;
    private final int discountValue;
    private final DiscountType discountType;
    private final int maxAmount;
    private final boolean used;
    private final LocalDateTime usedAt;
    private final LocalDateTime expiredAt;

    @Builder
    public MemberCouponResponse(Long memberCouponId, Long couponId, String name, int discountValue,
                                DiscountType discountType, int maxAmount, boolean used,
                                LocalDateTime expiredAt, LocalDateTime usedAt) {
        this.memberCouponId = memberCouponId;
        this.couponId = couponId;
        this.name = name;
        this.discountValue = discountValue;
        this.discountType = discountType;
        this.maxAmount = maxAmount;
        this.used = used;
        this.expiredAt = expiredAt;
        this.usedAt = usedAt;
    }

    public static MemberCouponResponse of(MemberCoupon memberCoupon) {
        Coupon coupon = memberCoupon.getCoupon();
        return MemberCouponResponse.builder()
                                   .memberCouponId(memberCoupon.getId())
                                   .couponId(coupon.getId())
                                   .name(coupon.getName())
                                   .discountType(coupon.getDiscountType())
                                   .discountValue(coupon.getDiscountValue())
                                   .maxAmount(coupon.getMaxAmount())
                                   .used(memberCoupon.isUsed())
                                   .expiredAt(coupon.getExpiredAt())
                                   .usedAt(memberCoupon.getUsedAt())
                                   .build();
    }

}
