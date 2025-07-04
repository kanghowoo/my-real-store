package com.myrealstore.profile.repository;

import static com.myrealstore.profile.domain.QProfile.profile;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.myrealstore.profile.domain.Profile;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProfileRepositoryImpl implements ProfileRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Profile> findProfilesWithSortType(Pageable pageable, OrderSpecifier<?> orderSpecifier) {
        List<Profile> profiles = queryFactory
                .selectFrom(profile)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long count = Optional.ofNullable(
                queryFactory
                        .select(profile.count())
                        .from(profile)
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(profiles, pageable, count);
    }
}
