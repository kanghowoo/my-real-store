package com.myrealstore.point.service.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PointEventServiceRequest {
    private Long memberId;
    int amount;
    String reason = "";

    @Builder
    public PointEventServiceRequest(Long memberId, int amount, String reason) {
        this.memberId = memberId;
        this.amount = amount;
        this.reason = reason;
    }

}
