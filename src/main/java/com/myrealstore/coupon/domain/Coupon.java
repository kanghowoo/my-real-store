package com.myrealstore.coupon.domain;

import java.time.LocalDateTime;

import com.myrealstore.global.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType discountType;

    @Column(name = "discount_value", nullable = false)
    private int discountValue;

    @Column(name = "max_amount", nullable = false)
    private int maxAmount;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    public int calculateDiscount(int originalAmount) {
        validateUsable();
        return discountType.calculate(originalAmount, discountValue, maxAmount);
    }

    public void validateUsable() {
        validateEnabled();
        validateNotExpired();
    }

    private void validateNotExpired() {
        if (this.expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("만료된 쿠폰입니다.");
        }
    }

    private void validateEnabled() {
        if (!enabled) {
            throw new IllegalStateException("사용 불가능한 쿠폰입니다.");
        }
    }
}
