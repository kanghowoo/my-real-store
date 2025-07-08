package com.myrealstore.coupon.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CouponTest {
    @Test
    @DisplayName("정액 쿠폰 - 지정된 금액만큼 할인된다")
    void fixedDiscount_shouldApplyCorrectly() {
        // given
        Coupon coupon = Coupon.builder()
                              .name("정액 쿠폰")
                              .discountType(DiscountType.FIXED)
                              .discountValue(3000)
                              .maxAmount(0)
                              .enabled(true)
                              .expiredAt(LocalDateTime.now().plusDays(1))
                              .build();
        // when
        int discounted = coupon.calculateDiscount(10000);

        // then
        assertThat(discounted).isEqualTo(3000);
    }

    @Test
    @DisplayName("정률 쿠폰 - 원금의 비율로 할인되며 최대 할인 금액을 넘지 않는다")
    void percentDiscount_shouldApplyWithMaxLimit() {
        Coupon coupon = Coupon.builder()
                              .name("정률 쿠폰")
                              .discountType(DiscountType.PERCENT)
                              .discountValue(20)
                              .maxAmount(5000)
                              .enabled(true)
                              .expiredAt(LocalDateTime.now().plusDays(1))
                              .build();

        int discounted1 = coupon.calculateDiscount(10000);
        int discounted2 = coupon.calculateDiscount(40000);

        assertThat(discounted1).isEqualTo(2000);
        assertThat(discounted2).isEqualTo(5000);
    }

    @Test
    @DisplayName("만료된 쿠폰은 할인 계산 시 예외 발생")
    void expiredCoupon_shouldThrowException() {
        Coupon coupon = Coupon.builder()
                              .name("만료 쿠폰")
                              .discountType(DiscountType.FIXED)
                              .discountValue(2000)
                              .expiredAt(LocalDateTime.now().minusDays(1))
                              .enabled(true)
                              .build();

        assertThatThrownBy(() -> coupon.calculateDiscount(10000))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("만료된 쿠폰입니다.");
    }

    @Test
    @DisplayName("비활성화된 쿠폰은 할인 계산 시 예외 발생")
    void disabledCoupon_shouldThrowException() {
        Coupon coupon = Coupon.builder()
                              .name("비활성 쿠폰")
                              .discountType(DiscountType.FIXED)
                              .discountValue(2000)
                              .enabled(false)
                              .expiredAt(LocalDateTime.now().plusDays(1))
                              .build();

        assertThatThrownBy(() -> coupon.calculateDiscount(10000))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("사용 불가능한 쿠폰입니다.");
    }
}
