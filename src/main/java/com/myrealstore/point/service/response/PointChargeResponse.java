package com.myrealstore.point.service.response;

import com.myrealstore.point.domain.Point;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PointChargeResponse {
    private Long memberId;
    int amount;

    @Builder
    public PointChargeResponse(Long memberId, int amount) {
        this.memberId = memberId;
        this.amount = amount;
    }

    public static PointChargeResponse of(Point point) {
        return PointChargeResponse.builder()
                                  .memberId(point.getMember().getId())
                                  .amount(point.getAmount())
                                  .build();
    }
}
