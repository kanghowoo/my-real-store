package com.myrealstore.membercoupon.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.myrealstore.global.common.exception.EntityNotFoundException;
import com.myrealstore.membercoupon.domain.MemberCoupon;
import com.myrealstore.membercoupon.repository.MemberCouponRepository;
import com.myrealstore.membercoupon.service.response.MemberCouponResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberCouponService {
    private final MemberCouponRepository memberCouponRepository;

    public List<MemberCouponResponse> getAvailableCoupons(Long memberId) {
        LocalDateTime now = LocalDateTime.now();
        return memberCouponRepository.findAvailableCouponsByMemberId(memberId, now).stream()
                                     .map(MemberCouponResponse::of)
                                     .toList();
    }

    public int applyCoupon(Long memberCouponId, int originalAmount) {
        MemberCoupon memberCoupon = memberCouponRepository.findById(memberCouponId)
                                                          .orElseThrow(
                                                                  () -> new EntityNotFoundException(
                                                                          "존재하지 않는 쿠폰입니다.")
                                                          );

        return memberCoupon.applyTo(originalAmount);
    }

    @Transactional
    public void useCoupon(Long memberCouponId) {
        boolean success = memberCouponRepository.markCouponUsed(memberCouponId, LocalDateTime.now());
        if (!success) {
            throw new IllegalStateException("이미 사용된 쿠폰입니다.");
        }
    }
}
