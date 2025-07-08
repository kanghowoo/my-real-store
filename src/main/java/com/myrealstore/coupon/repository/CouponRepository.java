package com.myrealstore.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myrealstore.coupon.domain.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
}
