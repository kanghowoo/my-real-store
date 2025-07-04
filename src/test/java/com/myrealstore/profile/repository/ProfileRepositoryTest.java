package com.myrealstore.profile.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.myrealstore.global.config.QuerydslConfig;
import com.myrealstore.profile.domain.Profile;
import com.myrealstore.profile.domain.ProfileSortType;

@DataJpaTest
@Import(QuerydslConfig.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
class ProfileRepositoryTest {

    @Autowired
    private ProfileRepository profileRepository;

    @DisplayName("프로필 목록을 '이름 가나다순' 으로 정렬한다.")
    @Test
    void findProfilesOrderByName() {
        // given
        saveProfile("홍길동", 2);
        saveProfile("이영희", 1);
        saveProfile("김철수", 0);

        Pageable pageable = PageRequest.of(0, 10);
        ProfileSortType profileSortType = ProfileSortType.from("NAME")
                                                         .orElse(ProfileSortType.getDefault());

        // when
        Page<Profile> profiles = profileRepository.findProfilesWithSortType(
                pageable, profileSortType.getOrderSpecifier());

        // then
        assertThat(profiles).hasSize(3)
                            .extracting(Profile::getName, Profile::getViewCount)
                            .containsExactly(
                                    tuple("김철수", 0),
                                    tuple("이영희", 1),
                                    tuple("홍길동", 2)
                            );
    }

    @DisplayName("프로필 목록을 '조회수' 로 정렬한다.")
    @Test
    void findProfilesOrderByViewCount() {
        // given
        saveProfile("홍길동", 2);
        saveProfile("이영희", 1);
        saveProfile("김철수", 0);

        Pageable pageable = PageRequest.of(0, 10);
        ProfileSortType profileSortType = ProfileSortType.from("VIEW")
                                                         .orElse(ProfileSortType.getDefault());

        // when
        Page<Profile> profiles = profileRepository.findProfilesWithSortType(
                pageable, profileSortType.getOrderSpecifier());

        // then
        assertThat(profiles).hasSize(3)
                            .extracting(Profile::getName, Profile::getViewCount)
                            .containsExactly(
                                    tuple("홍길동", 2),
                                    tuple("이영희", 1),
                                    tuple("김철수", 0)
                            );
    }

    @DisplayName("프로필 목록을 등록 '최신순' 으로 정렬한다.")
    @Test
    void findProfilesOrderByNewest() throws InterruptedException {
        // given
        saveProfileWithDelay("홍길동", 0);
        saveProfileWithDelay("이영희", 0);
        saveProfileWithDelay("김철수", 0);

        // when
        Pageable pageable = PageRequest.of(0, 10);
        ProfileSortType profileSortType = ProfileSortType.from("NEWEST")
                                                         .orElse(ProfileSortType.getDefault());

        Page<Profile> profiles = profileRepository.findProfilesWithSortType(
                pageable, profileSortType.getOrderSpecifier());

        // then
        assertThat(profiles).hasSize(3)
                .extracting(Profile::getName, Profile::getViewCount)
                .containsExactly(
                        tuple("김철수", 0),
                        tuple("이영희", 0),
                        tuple("홍길동", 0)
                );
    }

    @DisplayName("프로필 목록이 pagination 형태로 조회된다.")
    @Test
    void findProfilesWithPagination() {

        int bulkSize = 20;
        // given
        saveProfilesBulk(bulkSize);

        // when
        ProfileSortType profileSortType = ProfileSortType.getDefault();

        Page<Profile> page1 = profileRepository.findProfilesWithSortType(
                PageRequest.of(0, 10), profileSortType.getOrderSpecifier());

        Page<Profile> page2 = profileRepository.findProfilesWithSortType(
                PageRequest.of(0, 10), profileSortType.getOrderSpecifier());

        // then

        assertThat(page1.getTotalElements()).isEqualTo(20);
        assertThat(page1.hasNext()).isTrue();
        assertThat(page2.getContent()).hasSize(10);
    }

    private Profile saveProfile(String name, int viewCount) {
        Profile profile = new Profile(name, viewCount);
        return profileRepository.save(profile);
    }

    private Profile saveProfileWithDelay(String name, int viewCount) throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(10);
        return saveProfile(name, viewCount);
    }

    private void saveProfilesBulk(int size) {
        for (int i = 0; i < size; i++) {
            saveProfile("회원" + i, 0);
        }
    }

}
