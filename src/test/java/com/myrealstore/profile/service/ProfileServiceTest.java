package com.myrealstore.profile.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.myrealstore.profile.domain.Profile;
import com.myrealstore.profile.domain.ProfileSortType;
import com.myrealstore.profile.repository.ProfileRepository;
import com.myrealstore.profile.service.response.ProfileResponse;
import com.querydsl.core.types.OrderSpecifier;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final int DEFAULT_PAGE_SIZE = 10;
    @Mock
    ProfileRepository profileRepository;

    @InjectMocks
    ProfileService profileService;

    @DisplayName("주어진 파라미터값 정렬 기준에 따라 프로필 목록 조회를 한다.")
    @Test
    void getProfilesWithValidParam() {
        // given
        String paramName = "NAME";
        Pageable pageable = PageRequest.of(DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE);

        Profile profile1 = Profile.create("김철수");

        Page<Profile> profilePage = new PageImpl<>(List.of(profile1), pageable, 1);

        given(profileRepository.findProfilesWithSortType(any(Pageable.class), any(OrderSpecifier.class)))
                .willReturn(profilePage);

        ArgumentCaptor<OrderSpecifier> orderSpecifierCaptor = ArgumentCaptor.forClass(OrderSpecifier.class);

        // when
        Page<ProfileResponse> result = profileService.getProfiles(
                DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE, paramName);

        // then
        then(profileRepository).should()
                               .findProfilesWithSortType(any(Pageable.class), orderSpecifierCaptor.capture());
        assertThat(orderSpecifierCaptor.getValue()).isEqualTo(ProfileSortType.NAME.getOrderSpecifier());

        assertThat(result.getContent()).hasSize(1)
                                       .extracting(ProfileResponse::getName, ProfileResponse::getViewCount)
                                       .containsExactly(
                                               tuple("김철수", 0)
                                       );

        assertThat(result.getNumber()).isEqualTo(DEFAULT_PAGE_NUMBER);
        assertThat(result.getSize()).isEqualTo(DEFAULT_PAGE_SIZE);
    }

    @DisplayName("유효하지 않은 정렬 파라미터로 프로필 목록 조회 시도에도 기본 정렬 기준(최신순)으로 프로필 목록 조회를 한다.")
    @Test
    void getProfilesWithInValidParam() {
        // given
        String invalidParamName = "INVALID_SORT_KEY"; // 유효하지 않은 파라미터
        Pageable pageable = PageRequest.of(DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE);

        Profile profile = Profile.create("김철수");

        Page<Profile> profilePage = new PageImpl<>(List.of(profile), pageable, 1);

        given(profileRepository.findProfilesWithSortType(any(Pageable.class), any(OrderSpecifier.class)))
                .willReturn(profilePage);

        ArgumentCaptor<OrderSpecifier> orderSpecifierCaptor = ArgumentCaptor.forClass(OrderSpecifier.class);

        // when
        Page<ProfileResponse> result = profileService.getProfiles(
                DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE, invalidParamName);

        // then
        then(profileRepository).should()
                               .findProfilesWithSortType(any(Pageable.class), orderSpecifierCaptor.capture());
        assertThat(orderSpecifierCaptor.getValue()).isEqualTo(ProfileSortType.getDefault().getOrderSpecifier());

        assertThat(result.getContent()).hasSize(1)
                                       .extracting(ProfileResponse::getName, ProfileResponse::getViewCount)
                                       .containsExactly(
                                               tuple("김철수", 0)
                                       );

        assertThat(result.getNumber()).isEqualTo(DEFAULT_PAGE_NUMBER);
        assertThat(result.getSize()).isEqualTo(DEFAULT_PAGE_SIZE);
    }

}
