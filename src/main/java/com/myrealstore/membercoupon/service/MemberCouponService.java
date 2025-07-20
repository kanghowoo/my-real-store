package com.myrealstore.membercoupon.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.myrealstore.global.common.exception.EntityNotFoundException;
import com.myrealstore.membercoupon.domain.MemberCoupon;
import com.myrealstore.membercoupon.repository.MemberCouponRepository;
import com.myrealstore.membercoupon.service.request.ApplyCouponServiceRequest;
import com.myrealstore.membercoupon.service.request.UseCouponServiceRequest;
import com.myrealstore.membercoupon.service.response.ApplyCouponResponse;
import com.myrealstore.membercoupon.service.response.MemberCouponResponse;
import com.myrealstore.membercoupon.service.response.UseCouponResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberCouponService {
    private final MemberCouponRepository memberCouponRepository;

    public List<MemberCouponResponse> getAvailableCoupons(Long memberId, LocalDateTime now) {
        return memberCouponRepository.findAvailableCouponsByMemberId(memberId, now).stream()
                                     .map(MemberCouponResponse::of)
                                     .toList();
    }

    public ApplyCouponResponse applyCoupon(ApplyCouponServiceRequest request) {
        MemberCoupon memberCoupon = memberCouponRepository.findByIdForUpdate(request.getMemberCouponId())
                                                          .orElseThrow(() -> new EntityNotFoundException(
                                                                  "쿠폰이 존재하지 않습니다."));
        memberCoupon.verifyUsable(request.getMemberId());

        int discountAmount = memberCoupon.applyFor(request.getOriginalAmount());
        int finalAmount = request.getOriginalAmount() - discountAmount;

        return ApplyCouponResponse.builder()
                                  .memberCouponId(memberCoupon.getId())
                                  .discountAmount(discountAmount)
                                  .finalAmount(finalAmount)
                                  .description(memberCoupon.getCouponName())
                                  .build();
    }

    @Transactional
    public UseCouponResponse useCoupon(UseCouponServiceRequest request) {
        MemberCoupon memberCoupon = memberCouponRepository.findByIdForUpdate(request.getMemberCouponId())
                                                          .orElseThrow(() -> new EntityNotFoundException(
                                                                  "쿠폰이 존재하지 않습니다."));
        memberCoupon.verifyUsable(request.getMemberId());

        int discountAmount = memberCoupon.useFor(request.getOriginalAmount());
        int finalAmount = request.getOriginalAmount() - discountAmount;

        return UseCouponResponse.builder()
                                .memberCouponId(memberCoupon.getId())
                                .discountAmount(discountAmount)
                                .finalAmount(finalAmount)
                                .description(memberCoupon.getCouponName())
                                .build();
    }

}
