package com.myrealstore.point.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.myrealstore.global.common.exception.EntityNotFoundException;
import com.myrealstore.member.domain.Member;
import com.myrealstore.member.repository.MemberRepository;
import com.myrealstore.payment.exception.PointChargeException;
import com.myrealstore.point.domain.Point;
import com.myrealstore.point.repository.PointRepository;
import com.myrealstore.point.service.request.PointEventServiceRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void chargePoint(PointEventServiceRequest request) {
        int updated = memberRepository.increasePoint(request.getMemberId(), request.getAmount());
        if (updated == 0) {
            throw new PointChargeException("포인트 충전 실패");
        }

        Member member = memberRepository.findById(request.getMemberId())
                                        .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));

        pointRepository.save(Point.createCharge(member, request.getAmount(), request.getReason()));
    }

    @Transactional
    public void usePoint(PointEventServiceRequest request) {
        int updated = memberRepository.decreasePoint(request.getMemberId(), request.getAmount());
        if (updated == 0) {
            throw new IllegalStateException("포인트 사용 실패");
        }

        Member member = memberRepository.findById(request.getMemberId())
                                        .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));

        pointRepository.save(Point.createUse(member, request.getAmount(), request.getReason()));
    }
}
