package com.myrealstore.profile.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import com.myrealstore.global.common.exception.EntityNotFoundException;
import com.myrealstore.profile.domain.Profile;
import com.myrealstore.profile.repository.ProfileRepository;
import com.myrealstore.profile.service.request.ProfileSearchServiceRequest;
import com.myrealstore.profile.service.response.ProfileResponse;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@SpringBootTest
@Transactional
class ProfileServiceTest {

    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final int DEFAULT_PAGE_SIZE = 10;

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    ProfileService profileService;

    @PersistenceContext
    private EntityManager em;

    @DisplayName("주어진 파라미터값 정렬 기준에 따라 프로필 목록 조회를 한다.")
    @Test
    void getProfilesWithValidParam() {
        // given
        Profile profile1 = profileRepository.save(Profile.create("이영희"));
        Profile profile2 = profileRepository.save(Profile.create("김철수"));

        ProfileSearchServiceRequest request = ProfileSearchServiceRequest.builder()
                                                                         .page(DEFAULT_PAGE_NUMBER)
                                                                         .size(DEFAULT_PAGE_SIZE)
                                                                         .sort("NAME")
                                                                         .build();

        // when
        Page<ProfileResponse> result = profileService.getProfiles(request);

        // then
        assertThat(result.getContent()).hasSize(2)
                                       .extracting(ProfileResponse::getName, ProfileResponse::getViewCount)
                                       .containsExactly(
                                               tuple("김철수", 0),
                                               tuple("이영희", 0)
                                       );
    }

    @DisplayName("유효하지 않은 정렬 파라미터로 프로필 목록 조회 시도에도 기본 정렬 기준(최신순)으로 프로필 목록 조회를 한다.")
    @Test
    void getProfilesWithInValidParam() throws InterruptedException {
        // given
        saveProfileWithDelay("김철수", 0);
        saveProfileWithDelay("이영희", 1);

        ProfileSearchServiceRequest request = ProfileSearchServiceRequest.builder()
                                                                         .page(DEFAULT_PAGE_NUMBER)
                                                                         .size(DEFAULT_PAGE_SIZE)
                                                                         .sort("INVALID_KEY")
                                                                         .build();

        // when
        Page<ProfileResponse> result = profileService.getProfiles(request);

        // then
        assertThat(result.getContent()).hasSize(2)
                                       .extracting(ProfileResponse::getName, ProfileResponse::getViewCount)
                                       .containsExactly(
                                               tuple("이영희", 1),
                                               tuple("김철수", 0)
                                       );
    }

    @DisplayName("존재하는 프로필 상세조회 시, 조회수 1 증가")
    @Test
    void getProfileAndIncreaseView() {
        Profile saved = saveProfile("홍길동", 0);
        Long id = saved.getId();

        // when
        ProfileResponse response = profileService.getProfileAndIncreaseView(id);
        em.flush();
        em.clear();

        // then
        assertThat(response.getId()).isEqualTo(id);
        assertThat(profileRepository.findById(id).get().getViewCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("존재하지 않는 프로필 ID 조회 시 예외 발생")
    void getProfileAndIncreaseView_fail_notFound() {
        // given
        Long invalidId = 999L;

        // when & then
        assertThatThrownBy(() -> profileService.getProfileAndIncreaseView(invalidId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("해당 프로필을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("상세 조회 시 조회수 증가 후 목록 조회 시 반영되어 조회된다.")
    void increaseViewCountAndCheckInList() throws InterruptedException {
        // given
        Profile profile1 = saveProfileWithDelay("홍길동", 0);
        Profile profile2 = saveProfileWithDelay("김철수", 0);

        // when
        profileService.getProfileAndIncreaseView(profile1.getId());
        em.flush();
        em.clear();

        // then
        ProfileSearchServiceRequest request = ProfileSearchServiceRequest.builder()
                                                                         .page(0)
                                                                         .size(10)
                                                                         .sort("view")
                                                                         .build();

        Page<ProfileResponse> page = profileService.getProfiles(request);

        List<ProfileResponse> profiles = page.getContent();

        assertThat(profiles).hasSize(2);
        assertThat(profiles.get(0).getId()).isEqualTo(profile1.getId());
        assertThat(profiles.get(0).getViewCount()).isEqualTo(1);
        assertThat(profiles.get(1).getId()).isEqualTo(profile2.getId());
        assertThat(profiles.get(1).getViewCount()).isEqualTo(0);
    }

    private Profile saveProfile(String name, int viewCount) {
        Profile profile = Profile.builder()
                                 .name(name)
                                 .viewCount(viewCount)
                                 .build();

        return profileRepository.save(profile);
    }

    private Profile saveProfileWithDelay(String name, int viewCount) throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(10);
        return saveProfile(name, viewCount);
    }

}
