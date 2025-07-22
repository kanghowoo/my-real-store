package com.myrealstore.membercoupon.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.myrealstore.membercoupon.domain.MemberCoupon;

public interface MemberCouponRepositoryCustom {
    List<MemberCoupon> findAvailableCouponsByMemberId(Long memberId, LocalDateTime now);
    Optional<MemberCoupon> findByIdForUpdate(Long memberCouponId);

}
