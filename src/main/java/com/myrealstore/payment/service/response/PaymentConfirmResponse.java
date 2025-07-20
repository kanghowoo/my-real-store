package com.myrealstore.payment.service.response;

import com.myrealstore.payment.domain.Payment;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentConfirmResponse {
    private String paymentKey;
    private String orderId;
    private int finalAmount;
    private Integer discountAmount;
    private String status;

    @Builder
    public PaymentConfirmResponse(String paymentKey, String orderId, int finalAmount, Integer discountAmount,
                                  String status) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.finalAmount = finalAmount;
        this.discountAmount = discountAmount;
        this.status = status;
    }

    public static PaymentConfirmResponse of(Payment payment) {
        return PaymentConfirmResponse.builder()
                                     .orderId(payment.getOrderId())
                                     .paymentKey(payment.getPaymentKey())
                                     .status(payment.getStatus().name())
                                     .finalAmount(payment.getFinalAmount())
                                     .discountAmount(payment.getDiscountAmount())
                                     .build();
    }

    public boolean isPaymentDisApproved() {
        return !"DONE".equalsIgnoreCase(status);
    }
}
