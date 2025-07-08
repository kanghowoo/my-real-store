package com.myrealstore.membercoupon.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.myrealstore.coupon.domain.Coupon;
import com.myrealstore.coupon.domain.DiscountType;
import com.myrealstore.coupon.repository.CouponRepository;
import com.myrealstore.global.config.JpaAuditingConfig;
import com.myrealstore.global.config.QuerydslConfig;
import com.myrealstore.member.domain.Member;
import com.myrealstore.member.repository.MemberRepository;
import com.myrealstore.membercoupon.domain.MemberCoupon;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@DataJpaTest
@Import({ QuerydslConfig.class, JpaAuditingConfig.class })
@AutoConfigureTestDatabase(replace = Replace.NONE)
class MemberCouponRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private MemberCouponRepository memberCouponRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("사용 가능한 쿠폰 목록 조회 - 사용되지 않고, 만료되지 않고, enabled=true인 쿠폰만 반환한다")
    void findAvailableCoupons_ShouldReturnValidListOnly() {
        // given
        Member member = createMember("사용자");
        LocalDateTime now = LocalDateTime.of(2025, 7, 8, 10, 0);

        Coupon validCoupon = createCoupon(
                "정상 쿠폰", DiscountType.FIXED, 3000, 0, now.plusDays(1), true);

        Coupon expiredCoupon = createCoupon(
                "만료 쿠폰", DiscountType.FIXED, 3000, 0, now.minusDays(1), true);

        Coupon disabledCoupon = createCoupon(
                "비활성 쿠폰", DiscountType.FIXED, 3000, 0, now.plusDays(1), false);

        createMemberCoupon(member, validCoupon, false, null);
        createMemberCoupon(member, expiredCoupon, false, null);
        createMemberCoupon(member, disabledCoupon, false, null);

        // when
        List<MemberCoupon> result = memberCouponRepository.findAvailableCouponsByMemberId(member.getId(), now);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCoupon().getName()).isEqualTo("정상 쿠폰");
    }

    @Test
    @DisplayName("사용되지 않은 쿠폰만 업데이트된다")
    void markCouponUsed_ShouldOnlyUpdateWhenNotUsed() {
        // given
        Member member = createMember("사용자");
        Coupon coupon = createCoupon("정상 쿠폰", DiscountType.FIXED, 3000, 0,
                                     LocalDateTime.now().plusDays(3), true);

        MemberCoupon memberCoupon = createMemberCoupon(member, coupon, false, null);

        em.flush();
        em.clear();

        // when
        boolean success = memberCouponRepository.markCouponUsed(memberCoupon.getId(), LocalDateTime.now());

        // then
        assertThat(success).isTrue();

        MemberCoupon updated = memberCouponRepository.findById(memberCoupon.getId()).orElseThrow();
        assertThat(updated.isUsed()).isTrue();
        assertThat(updated.getUsedAt()).isNotNull();
    }

    @Test
    @DisplayName("이미 사용된 쿠폰은 업데이트되지 않는다")
    void markCouponUsed_ShouldFailWhenAlreadyUsed() {
        // given
        Member member = createMember("사용자");
        Coupon coupon =
                createCoupon("사용됨", DiscountType.FIXED, 3000, 0,
                             LocalDateTime.now().plusDays(3), true);

        MemberCoupon memberCoupon =
                createMemberCoupon(member, coupon, true, LocalDateTime.now().minusDays(1));

        // when
        boolean success = memberCouponRepository.markCouponUsed(memberCoupon.getId(), LocalDateTime.now());

        // then
        assertThat(success).isFalse();
    }

    // 테스트 픽스처 메서드들

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
