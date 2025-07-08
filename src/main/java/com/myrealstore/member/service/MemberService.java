package com.myrealstore.member.service;

import org.springframework.stereotype.Service;

import com.myrealstore.global.common.exception.EntityNotFoundException;
import com.myrealstore.member.domain.Member;
import com.myrealstore.member.repository.MemberRepository;
import com.myrealstore.member.service.response.MemberResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberResponse getMember(Long id) {
        Member member = memberRepository.findById(id)
                                        .orElseThrow(() -> new EntityNotFoundException("해당 회원이 존재하지 않습니다."));
        return MemberResponse.of(member);
    }
}

