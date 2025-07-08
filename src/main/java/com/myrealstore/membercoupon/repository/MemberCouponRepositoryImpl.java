package com.myrealstore.membercoupon.repository;

import static com.myrealstore.coupon.domain.QCoupon.coupon;
import static com.myrealstore.member.domain.QMember.member;
import static com.myrealstore.membercoupon.domain.QMemberCoupon.memberCoupon;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.myrealstore.membercoupon.domain.MemberCoupon;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemberCouponRepositoryImpl implements MemberCouponRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<MemberCoupon> findAvailableCoupons(Long memberId, LocalDateTime now) {
        return queryFactory
                .selectFrom(memberCoupon)
                .join(memberCoupon.coupon, coupon).fetchJoin()
                .where(
                        memberCoupon.member.id.eq(memberId),
                        memberCoupon.used.isFalse(),
                        memberCoupon.coupon.enabled.isTrue(),
                        memberCoupon.coupon.expiredAt.after(now)
                )
                .orderBy(memberCoupon.coupon.expiredAt.asc())
                .fetch();
    }

    @Override
    public boolean markCouponUsed(Long memberCouponId, LocalDateTime usedAt) {

        long updated = queryFactory
                .update(memberCoupon)
                .set(memberCoupon.used, true)
                .set(memberCoupon.usedAt, usedAt)
                .where(memberCoupon.id.eq(memberCouponId).and(memberCoupon.used.isFalse()))
                .execute();

        return updated == 1;
    }
}
