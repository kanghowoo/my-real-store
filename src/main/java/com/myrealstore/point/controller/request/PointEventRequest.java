package com.myrealstore.point.controller.request;

import com.myrealstore.point.service.request.PointEventServiceRequest;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PointEventRequest {
    @NotNull(message = "회원 ID는 필수입니다.")
    private Long memberId;

    @Min(value = 1, message = "충전 금액은 1 이상이어야 합니다.")
    int amount;

    String reason = "";

    @Builder
    public PointEventRequest(Long memberId, int amount, String reason) {
        this.memberId = memberId;
        this.amount = amount;
        this.reason = reason;
    }

    public PointEventServiceRequest toServiceRequest() {
        return PointEventServiceRequest.builder()
                .memberId(memberId)
                .amount(amount)
                .reason(reason)
                .build();
    }

}
