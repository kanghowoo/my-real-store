package com.myrealstore.membercoupon.controller.request;

import com.myrealstore.membercoupon.service.request.ApplyCouponServiceRequest;
import com.myrealstore.membercoupon.service.request.UseCouponServiceRequest;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UseCouponRequest {
    private Long memberId; // todo LoginMember's ID
    private Long memberCouponId;
    private Integer originalAmount;

    @Builder
    public UseCouponRequest(Long memberId, Long memberCouponId, Integer originalAmount) {
        this.memberId = memberId;
        this.memberCouponId = memberCouponId;
        this.originalAmount = originalAmount;
    }

    public UseCouponServiceRequest toServiceRequest() {
        return UseCouponServiceRequest.builder()
                                        .memberId(memberId)
                                        .memberCouponId(memberCouponId)
                                        .originalAmount(originalAmount)
                                        .build();
    }
}
