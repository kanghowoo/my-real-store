package com.myrealstore.membercoupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.myrealstore.membercoupon.domain.MemberCoupon;

@Repository
public interface MemberCouponRepository
        extends JpaRepository<MemberCoupon, Long>, MemberCouponRepositoryCustom {
}
