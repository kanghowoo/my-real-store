package com.myrealstore.profile.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.myrealstore.profile.domain.ProfileSortType;
import com.myrealstore.profile.repository.ProfileRepository;
import com.myrealstore.profile.service.response.ProfileResponse;
import com.querydsl.core.types.OrderSpecifier;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {

    private final ProfileRepository profileRepository;

    public Page<ProfileResponse> getProfiles(int page, int size, String paramName) {
        Pageable pageable = PageRequest.of(page, size);
        ProfileSortType profileSortType = ProfileSortType.from(paramName)
                                                         .orElse(ProfileSortType.getDefault());
        OrderSpecifier<?> orderSpecifier = profileSortType.getOrderSpecifier();

        return profileRepository.findProfilesWithSortType(pageable, orderSpecifier)
                                .map(ProfileResponse::of);
    }
}
