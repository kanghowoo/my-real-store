package com.myrealstore.member.repository;

import static com.myrealstore.member.domain.QMember.member;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public int increasePoint(Long memberId, int amount) {
        return (int) queryFactory.update(member)
                           .set(member.point, member.point.add(amount))
                           .where(member.id.eq(memberId))
                           .execute();
    }

    @Override
    public int decreasePoint(Long memberId, int amount) {
        return (int) queryFactory.update(member)
                           .set(member.point, member.point.subtract(amount))
                           .where(member.id.eq(memberId)
                                           .and(member.point.goe(amount)))
                           .execute();
    }
}
