package com.myrealstore.membercoupon.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.myrealstore.coupon.domain.Coupon;
import com.myrealstore.coupon.domain.DiscountType;
import com.myrealstore.coupon.repository.CouponRepository;
import com.myrealstore.member.domain.Member;
import com.myrealstore.member.repository.MemberRepository;
import com.myrealstore.membercoupon.domain.MemberCoupon;
import com.myrealstore.membercoupon.repository.MemberCouponRepository;
import com.myrealstore.membercoupon.service.request.ApplyCouponServiceRequest;
import com.myrealstore.membercoupon.service.request.UseCouponServiceRequest;
import com.myrealstore.membercoupon.service.response.ApplyCouponResponse;
import com.myrealstore.membercoupon.service.response.MemberCouponResponse;
import com.myrealstore.membercoupon.service.response.UseCouponResponse;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
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
    void getAvailableCoupons_validCouponsOnly() {
        // given
        Member member = createMember("회원1");
        LocalDateTime now = LocalDateTime.now();

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
        List<MemberCouponResponse> result = memberCouponService.getAvailableCoupons(member.getId(), LocalDateTime.now());

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("정상 쿠폰");
    }

    @Test
    @DisplayName("정액 쿠폰이 정상 적용된다(사용X)")
    void applyCoupon_withFixedDiscount() {
        // given
        Member member = createMember("회원2");
        Coupon coupon = createCoupon("5000원 할인", DiscountType.FIXED, 5000, 0,
                                     LocalDateTime.now().plusDays(1), true);
        MemberCoupon memberCoupon = createMemberCoupon(member, coupon, false, null);

        ApplyCouponServiceRequest request = ApplyCouponServiceRequest.builder()
                                                                     .memberId(member.getId())
                                                                     .memberCouponId(memberCoupon.getId())
                                                                     .originalAmount(10000)
                                                                     .build();

        // when
        ApplyCouponResponse response = memberCouponService.applyCoupon(request);

        em.flush();
        em.clear();

        // then
        assertThat(response.getDiscountAmount()).isEqualTo(5000);
        assertThat(response.getFinalAmount()).isEqualTo(5000);
        assertThat(response.getMemberCouponId()).isEqualTo(memberCoupon.getId());

        MemberCoupon notUpdated = memberCouponRepository.findById(memberCoupon.getId()).orElseThrow();
        assertThat(notUpdated.isUsed()).isFalse();
        assertThat(notUpdated.getUsedAt()).isNull();
    }

    @Test
    @DisplayName("정률 쿠폰이 정상 적용된다(사용X) - 최대 할인 금액도 고려")
    void applyCoupon_withPercentDiscount() {
        // given
        Member member = createMember("회원3");
        Coupon coupon = createCoupon("20% 할인", DiscountType.PERCENT, 20, 3000,
                                     LocalDateTime.now().plusDays(1), true);
        MemberCoupon memberCoupon = createMemberCoupon(member, coupon, false, null);

        ApplyCouponServiceRequest request = ApplyCouponServiceRequest.builder()
                                                                     .memberId(member.getId())
                                                                     .memberCouponId(memberCoupon.getId())
                                                                     .originalAmount(20000)
                                                                     .build();

        // when
        ApplyCouponResponse response = memberCouponService.applyCoupon(request);

        em.flush();
        em.clear();

        // then
        assertThat(response.getDiscountAmount()).isEqualTo(3000);
        assertThat(response.getFinalAmount()).isEqualTo(17000);
        assertThat(response.getMemberCouponId()).isEqualTo(memberCoupon.getId());

        MemberCoupon updated = memberCouponRepository.findById(memberCoupon.getId()).orElseThrow();
        assertThat(updated.isUsed()).isFalse();
        assertThat(updated.getUsedAt()).isNull();
    }

    @Test
    @DisplayName("이미 사용된 쿠폰은 applyCoupon에서 예외 발생")
    void applyCoupon_whenAlreadyUsed() {
        // given
        Member member = createMember("회원4");
        Coupon coupon = createCoupon("이미 사용됨", DiscountType.FIXED, 3000, 0,
                                     LocalDateTime.now().plusDays(1), true);
        MemberCoupon memberCoupon = createMemberCoupon(member, coupon, true, LocalDateTime.now().minusDays(1));

        ApplyCouponServiceRequest request = ApplyCouponServiceRequest.builder()
                                                                     .memberId(member.getId())
                                                                     .memberCouponId(memberCoupon.getId())
                                                                     .originalAmount(10000)
                                                                     .build();

        // then
        assertThatThrownBy(() -> memberCouponService.applyCoupon(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 사용된 쿠폰");
    }

    @Test
    @DisplayName("정상적인 쿠폰은 useCoupon으로 사용 처리할 수 있다")
    void useCoupon() {
        // given
        Member member = createMember("회원5");
        Coupon coupon = createCoupon("사용 가능 쿠폰", DiscountType.FIXED, 2000, 0,
                                     LocalDateTime.now().plusDays(2), true);
        MemberCoupon memberCoupon = createMemberCoupon(member, coupon, false, null);

        UseCouponServiceRequest request = UseCouponServiceRequest.builder()
                                                                 .memberCouponId(memberCoupon.getId())
                                                                 .memberId(member.getId())
                                                                 .originalAmount(10000)
                                                                 .build();

        // when
        UseCouponResponse response = memberCouponService.useCoupon(request);
        em.flush();
        em.clear();

        // then
        assertThat(response.getDiscountAmount()).isEqualTo(2000);
        assertThat(response.getFinalAmount()).isEqualTo(8000);

        MemberCoupon updated = memberCouponRepository.findById(memberCoupon.getId()).orElseThrow();
        assertThat(updated.isUsed()).isTrue();
        assertThat(updated.getUsedAt()).isNotNull();
    }

    @Test
    @DisplayName("이미 사용된 쿠폰은 useCoupon에서 예외 발생")
    void useCoupon_whenAlreadyUsed() {
        // given
        Member member = createMember("회원6");
        Coupon coupon = createCoupon("중복 사용", DiscountType.FIXED, 2000, 0,
                                     LocalDateTime.now().plusDays(2), true);
        MemberCoupon memberCoupon = createMemberCoupon(member, coupon, true, LocalDateTime.now().minusHours(1));

        UseCouponServiceRequest request = UseCouponServiceRequest.builder()
                                                                 .memberCouponId(memberCoupon.getId())
                                                                 .memberId(member.getId())
                                                                 .originalAmount(10000)
                                                                 .build();

        // then
        assertThatThrownBy(() -> memberCouponService.useCoupon(request))
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
