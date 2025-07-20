package com.myrealstore.payment.domain;

import com.myrealstore.global.common.BaseEntity;
import com.myrealstore.payment.service.request.PaymentCreateServiceRequest;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "payment", uniqueConstraints = {
        @UniqueConstraint(name = "uk_payment_order_id", columnNames = "order_id"),
        @UniqueConstraint(name = "uk_payment_key", columnNames = "payment_key")
})
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;
    private String orderId;
    private String paymentKey;

    private Integer finalAmount;
    private Integer discountAmount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    public static Payment createReadyPayment(PaymentCreateServiceRequest request) {
        return Payment.builder()
                      .memberId(request.getMemberId())
                      .orderId(request.getOrderId())
                      .finalAmount(request.getFinalAmount())
                      .discountAmount(request.getDiscountAmount())
                      .status(PaymentStatus.READY)
                      .build();
    }

    public void confirm(String paymentKey) {
        this.paymentKey = paymentKey;
        this.status = PaymentStatus.DONE;
    }

    public void fail() {
        this.status = PaymentStatus.FAILED;
    }

}
