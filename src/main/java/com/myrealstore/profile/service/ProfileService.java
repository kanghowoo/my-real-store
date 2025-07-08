package com.myrealstore.profile.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.myrealstore.global.common.exception.EntityNotFoundException;
import com.myrealstore.profile.domain.Profile;
import com.myrealstore.profile.domain.ProfileSortType;
import com.myrealstore.profile.repository.ProfileRepository;
import com.myrealstore.profile.service.request.ProfileSearchServiceRequest;
import com.myrealstore.profile.service.response.ProfileResponse;
import com.querydsl.core.types.OrderSpecifier;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {

    private final ProfileRepository profileRepository;

    public Page<ProfileResponse> getProfiles(ProfileSearchServiceRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        ProfileSortType profileSortType = ProfileSortType.from(request.getSort())
                                                         .orElse(ProfileSortType.getDefault());
        OrderSpecifier<?> orderSpecifier = profileSortType.getOrderSpecifier();

        return profileRepository.findProfilesWithSortType(pageable, orderSpecifier)
                                .map(ProfileResponse::of);
    }

    @Transactional
    public ProfileResponse getProfileAndIncreaseView(Long profileId) {
        profileRepository.increaseViewCount(profileId);

        Profile profile = profileRepository.findById(profileId)
                                           .orElseThrow(
                                                   () -> new EntityNotFoundException("해당 프로필을 찾을 수 없습니다.")
                                           );

        return ProfileResponse.of(profile);
    }
}
