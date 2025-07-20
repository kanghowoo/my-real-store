package com.myrealstore.membercoupon.repository;

import static com.myrealstore.coupon.domain.QCoupon.coupon;
import static com.myrealstore.membercoupon.domain.QMemberCoupon.memberCoupon;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.myrealstore.membercoupon.domain.MemberCoupon;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemberCouponRepositoryImpl implements MemberCouponRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<MemberCoupon> findAvailableCouponsByMemberId(Long memberId, LocalDateTime now) {
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
    public Optional<MemberCoupon> findByIdForUpdate(Long memberCouponId) {
        MemberCoupon coupon = queryFactory
                .selectFrom(memberCoupon)
                .where(memberCoupon.id.eq(memberCouponId))
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)  // select for update 락
                .fetchOne();

        return Optional.ofNullable(coupon);
    }

}
