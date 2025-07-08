package com.myrealstore.membercoupon.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.myrealstore.coupon.domain.Coupon;
import com.myrealstore.coupon.domain.DiscountType;
import com.myrealstore.member.domain.Member;

class MemberCouponTest {

    private final LocalDateTime future = LocalDateTime.now().plusDays(2);

    @Test
    @DisplayName("정상 쿠폰 적용 시 할인 금액 반환 + 상태 변경된다")
    void applyTo_shouldReturnDiscountAndMarkUsed() {
        // given
        Coupon coupon = createCoupon("정액 쿠폰", DiscountType.FIXED, 3000, 0, future, true);
        MemberCoupon memberCoupon = createMemberCoupon(coupon, false);

        // when
        int discount = memberCoupon.applyTo(10000);

        // then
        assertThat(discount).isEqualTo(3000);
        assertThat(memberCoupon.isUsed()).isTrue();
        assertThat(memberCoupon.getUsedAt()).isNotNull();
    }

    @Test
    @DisplayName("이미 사용된 쿠폰은 applyTo 시 예외 발생")
    void applyTo_shouldFail_whenAlreadyUsed() {
        // given
        Coupon coupon = createCoupon("정액 쿠폰", DiscountType.FIXED, 3000, 0, future, true);
        MemberCoupon memberCoupon = createMemberCoupon(coupon, true);

        // when & then
        assertThatThrownBy(() -> memberCoupon.applyTo(10000))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 사용된 쿠폰입니다.");
    }

    @Test
    @DisplayName("비활성 쿠폰은 applyTo 시 예외 발생")
    void applyTo_shouldFail_whenCouponDisabled() {
        // given
        Coupon coupon = createCoupon("비활성 쿠폰", DiscountType.FIXED, 3000, 0, future, false);
        MemberCoupon memberCoupon = createMemberCoupon(coupon, false);

        // when & then
        assertThatThrownBy(() -> memberCoupon.applyTo(10000))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("사용 불가능한 쿠폰입니다.");
    }

    @Test
    @DisplayName("만료된 쿠폰은 applyTo 시 예외 발생")
    void applyTo_shouldFail_whenCouponExpired() {
        // given
        Coupon coupon = createCoupon("만료 쿠폰", DiscountType.FIXED, 3000, 0, LocalDateTime.now().minusDays(1), true);
        MemberCoupon memberCoupon = createMemberCoupon(coupon, false);

        // when & then
        assertThatThrownBy(() -> memberCoupon.applyTo(10000))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("만료된 쿠폰입니다.");
    }

    private Member createMember(String name) {
        return Member.builder().name(name).build();
    }

    private Coupon createCoupon(String name, DiscountType type, int discountValue, int maxAmount,
                                LocalDateTime expiredAt, boolean enabled) {
        return Coupon.builder()
                     .name(name)
                     .discountType(type)
                     .discountValue(discountValue)
                     .maxAmount(maxAmount)
                     .expiredAt(expiredAt)
                     .enabled(enabled)
                     .build();
    }

    private MemberCoupon createMemberCoupon(Coupon coupon, boolean used) {
        return MemberCoupon.builder()
                           .member(createMember("테스트 회원"))
                           .coupon(coupon)
                           .used(used)
                           .build();
    }

}
