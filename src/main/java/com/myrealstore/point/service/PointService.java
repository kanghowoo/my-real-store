package com.myrealstore.point.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.myrealstore.global.common.exception.EntityNotFoundException;
import com.myrealstore.member.domain.Member;
import com.myrealstore.member.repository.MemberRepository;
import com.myrealstore.membercoupon.service.MemberCouponService;
import com.myrealstore.membercoupon.service.request.UseCouponServiceRequest;
import com.myrealstore.point.domain.Point;
import com.myrealstore.point.repository.PointRepository;
import com.myrealstore.point.service.request.PointChargeServiceRequest;
import com.myrealstore.point.service.response.PointChargeResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PointService {

    private final MemberCouponService memberCouponService;

    private final PointRepository pointRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public PointChargeResponse chargePoint(PointChargeServiceRequest request) {

        UseCouponServiceRequest couponRequest = UseCouponServiceRequest.builder()
                                                               .memberId(request.getMemberId())
                                                               .memberCouponId(request.getMemberCouponId())
                                                               .build();

        if (hasMemberCoupon(request)) {
            memberCouponService.useCoupon(couponRequest);
        }

        // 포인트 적립
        Member member = memberRepository.findById(request.getMemberId())
                                        .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));

        member.increasePoint(request.getAmount());
        memberRepository.save(member);

        Point saved = pointRepository.save(Point.createCharge(member, request.getAmount(), request.getReason()));

        return PointChargeResponse.of(saved);
    }

    @Transactional
    public void usePoint(PointChargeServiceRequest request) {
        int updated = memberRepository.decreasePoint(request.getMemberId(), request.getAmount());
        if (updated == 0) {
            throw new IllegalStateException("포인트 사용 실패");
        }

        Member member = memberRepository.findById(request.getMemberId())
                                        .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));

        pointRepository.save(Point.createUse(member, request.getAmount(), request.getReason()));
    }

    private static boolean hasMemberCoupon(PointChargeServiceRequest request) {
        return request.getMemberCouponId() != null;
    }
}
