package com.myrealstore.membercoupon.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.myrealstore.coupon.domain.Coupon;
import com.myrealstore.coupon.domain.DiscountType;
import com.myrealstore.coupon.repository.CouponRepository;
import com.myrealstore.member.domain.Member;
import com.myrealstore.member.repository.MemberRepository;
import com.myrealstore.membercoupon.domain.MemberCoupon;
import com.myrealstore.membercoupon.repository.MemberCouponRepository;
import com.myrealstore.membercoupon.service.response.MemberCouponResponse;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@SpringBootTest
@Transactional
class MemberCouponServiceTest {

    @Autowired
    private MemberCouponService memberCouponService;

    @Autowired
    private MemberCouponRepository memberCouponRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CouponRepository couponRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("사용 가능한 쿠폰만 조회된다 - 사용X, 만료X, enabled=true")
    void getAvailableCoupons_returnsValidCouponsOnly() {
        // given
        Member member = createMember("회원1");
        LocalDateTime now = LocalDateTime.of(2025, 7, 8, 10, 0);

        Coupon valid =
                createCoupon("정상 쿠폰", DiscountType.FIXED, 3000, 0, now.plusDays(1), true);
        Coupon expired =
                createCoupon("만료 쿠폰", DiscountType.FIXED, 3000, 0, now.minusDays(1), true);
        Coupon disabled =
                createCoupon("비활성 쿠폰", DiscountType.FIXED, 3000, 0, now.plusDays(1), false);

        createMemberCoupon(member, valid, false, null);
        createMemberCoupon(member, expired, false, null);
        createMemberCoupon(member, disabled, false, null);

        // when
        List<MemberCouponResponse> result = memberCouponService.getAvailableCoupons(member.getId());

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("정상 쿠폰");
    }

    @Test
    @DisplayName("정액 쿠폰이 정상 적용된다")
    void applyCoupon_withFixedDiscount_shouldApplyCorrectly() {
        // given
        Member member = createMember("회원2");
        Coupon coupon = createCoupon("5000원 할인", DiscountType.FIXED, 5000, 0,
                                     LocalDateTime.now().plusDays(1), true);
        MemberCoupon mc = createMemberCoupon(member, coupon, false, null);

        // when
        int discounted = memberCouponService.applyCoupon(mc.getId(), 10000);

        // then
        assertThat(discounted).isEqualTo(5000);

        MemberCoupon updated = memberCouponRepository.findById(mc.getId()).orElseThrow();
        assertThat(updated.isUsed()).isTrue();
        assertThat(updated.getUsedAt()).isNotNull();
    }

    @Test
    @DisplayName("정률 쿠폰이 정상 적용된다 - 최대 할인 금액도 고려")
    void applyCoupon_withPercentDiscount_shouldApplyCorrectly() {
        // given
        Member member = createMember("회원3");
        Coupon coupon = createCoupon("20% 할인", DiscountType.PERCENT, 20, 3000,
                                     LocalDateTime.now().plusDays(1), true);
        MemberCoupon mc = createMemberCoupon(member, coupon, false, null);

        // when
        int discounted = memberCouponService.applyCoupon(mc.getId(), 20000);

        // then
        assertThat(discounted).isEqualTo(3000);

        MemberCoupon updated = memberCouponRepository.findById(mc.getId()).orElseThrow();
        assertThat(updated.isUsed()).isTrue();
        assertThat(updated.getUsedAt()).isNotNull();
    }

    @Test
    @DisplayName("이미 사용된 쿠폰은 applyCoupon에서 예외 발생")
    void applyCoupon_shouldThrow_whenAlreadyUsed() {
        // given
        Member member = createMember("회원4");
        Coupon coupon = createCoupon("이미 사용됨", DiscountType.FIXED, 3000, 0,
                                     LocalDateTime.now().plusDays(1), true);
        MemberCoupon mc = createMemberCoupon(member, coupon, true, LocalDateTime.now().minusDays(1));

        // expect
        assertThatThrownBy(() -> memberCouponService.applyCoupon(mc.getId(), 10000))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 사용된 쿠폰");
    }

    @Test
    @DisplayName("정상적인 쿠폰은 useCoupon으로 사용 처리할 수 있다")
    void useCoupon_shouldMarkAsUsed() {
        // given
        Member member = createMember("회원5");
        Coupon coupon = createCoupon("사용 가능 쿠폰", DiscountType.FIXED, 2000, 0,
                                     LocalDateTime.now().plusDays(2), true);
        MemberCoupon memberCoupon = createMemberCoupon(member, coupon, false, null);

        // when
        memberCouponService.useCoupon(memberCoupon.getId());
        em.flush();
        em.clear();

        // then
        MemberCoupon updated = memberCouponRepository.findById(memberCoupon.getId()).orElseThrow();
        assertThat(updated.isUsed()).isTrue();
        assertThat(updated.getUsedAt()).isNotNull();
    }

    @Test
    @DisplayName("이미 사용된 쿠폰은 useCoupon에서 예외 발생")
    void useCoupon_shouldThrow_whenAlreadyUsed() {
        // given
        Member member = createMember("회원6");
        Coupon coupon = createCoupon("중복 사용", DiscountType.FIXED, 2000, 0,
                                     LocalDateTime.now().plusDays(2), true);
        MemberCoupon mc = createMemberCoupon(member, coupon, true, LocalDateTime.now().minusHours(1));

        // expect
        assertThatThrownBy(() -> memberCouponService.useCoupon(mc.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 사용된 쿠폰");
    }

    private Member createMember(String name) {
        return memberRepository.save(Member.builder().name(name).build());
    }

    private Coupon createCoupon(String name, DiscountType type, int discountValue, int maxAmount,
                                LocalDateTime expiredAt, boolean enabled) {
        return couponRepository.save(Coupon.builder()
                                           .name(name)
                                           .discountType(type)
                                           .discountValue(discountValue)
                                           .maxAmount(maxAmount)
                                           .expiredAt(expiredAt)
                                           .enabled(enabled)
                                           .build());
    }

    private MemberCoupon createMemberCoupon(Member member, Coupon coupon, boolean used, LocalDateTime usedAt) {
        return memberCouponRepository.save(MemberCoupon.builder()
                                                       .member(member)
                                                       .coupon(coupon)
                                                       .used(used)
                                                       .usedAt(usedAt)
                                                       .build());
    }
}
