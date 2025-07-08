package com.myrealstore.member.repository;

public interface MemberRepositoryCustom {
    int increasePoint(Long memberId, int amount);
    int decreasePoint(Long memberId, int amount);
}
