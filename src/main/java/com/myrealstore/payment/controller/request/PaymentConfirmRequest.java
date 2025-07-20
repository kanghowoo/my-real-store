package com.myrealstore.payment.controller.request;

import com.myrealstore.payment.service.request.PaymentConfirmServiceRequest;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaymentConfirmRequest {

    @NotNull(message = "회원 ID는 필수입니다.")
    private Long memberId;

    @NotBlank(message = "결제 키는 필수입니다.")
    private String paymentKey;

    @NotBlank(message = "주문 ID는 필수입니다.")
    private String orderId;

    @Min(value = 1, message = "결제 금액은 1원 이상이어야 합니다.")
    private int amount;

    @NotBlank(message = "결제 시스템 제공자는 필수입니다.")
    private String provider;

    private Long memberCouponId;

    @Builder
    public PaymentConfirmRequest(Long memberId, String paymentKey, String orderId, int amount,
                                 String provider, Long memberCouponId) {
        this.memberId = memberId;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.provider = provider;
        this.memberCouponId = memberCouponId;
    }

    public PaymentConfirmServiceRequest toServiceRequest() {
        return PaymentConfirmServiceRequest.builder()
                                           .memberId(memberId)
                                           .paymentKey(paymentKey)
                                           .orderId(orderId)
                                           .amount(amount)
                                           .provider(provider)
                                           .memberCouponId(memberCouponId)
                                           .build();
    }
}
