package com.myrealstore.point.service.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PointChargeServiceRequest {
    private Long memberId;
    private Long memberCouponId;
    private int amount;
    private String reason = "";

    @Builder
    public PointChargeServiceRequest(Long memberId, Long memberCouponId, int amount, String reason) {
        this.memberId = memberId;
        this.memberCouponId = memberCouponId;
        this.amount = amount;
        this.reason = reason;
    }

}
