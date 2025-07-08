package com.myrealstore.profile.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.myrealstore.profile.domain.Profile;
import com.querydsl.core.types.OrderSpecifier;

public interface ProfileRepositoryCustom {
    Page<Profile> findProfilesWithSortType(Pageable pageable, OrderSpecifier<?> orderSpecifier);
    void increaseViewCount(Long profileId);
}
