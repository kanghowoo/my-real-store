package com.myrealstore.point.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.myrealstore.global.common.exception.EntityNotFoundException;
import com.myrealstore.member.domain.Member;
import com.myrealstore.member.repository.MemberRepository;
import com.myrealstore.point.domain.Point;
import com.myrealstore.point.repository.PointRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void chargePoint(Long memberId, int amount) {
        Member member = memberRepository.findById(memberId)
                                        .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));

        member.increasePoint(amount);
        pointRepository.save(Point.createCharge(member, amount, "TEST"));
    }

    @Transactional
    public void usePoint(Long memberId, int amount) {
        Member member = memberRepository.findById(memberId)
                                        .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));

        member.decreasePoint(amount);
        pointRepository.save(Point.createUse(member, amount, "TEST"));
    }
}
