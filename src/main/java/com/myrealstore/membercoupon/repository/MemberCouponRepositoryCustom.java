package com.myrealstore.membercoupon.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.myrealstore.membercoupon.domain.MemberCoupon;

public interface MemberCouponRepositoryCustom {
    List<MemberCoupon> findAvailableCouponsByMemberId(Long memberId, LocalDateTime now);
    boolean markCouponUsed(Long memberCouponId, LocalDateTime usedAt);

}
