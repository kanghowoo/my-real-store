package com.myrealstore.profile.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProfileSortTypeTest {
    @DisplayName("유효한 paramName으로 from 메서드 호출 시 올바른 ProfileSortType을 반환한다.")
    @Test
    void from_withValidParamName() {
        // given
        String nameParam = "name";
        String viewParam = "VIEW";
        String newestParam = "newest ";

        // when
        Optional<ProfileSortType> nameSortType = ProfileSortType.from(nameParam);
        Optional<ProfileSortType> viewSortType = ProfileSortType.from(viewParam);
        Optional<ProfileSortType> newestSortType = ProfileSortType.from(newestParam);

        // then
        assertThat(nameSortType).isPresent();
        assertThat(nameSortType.get()).isEqualTo(ProfileSortType.NAME);

        assertThat(viewSortType).isPresent();
        assertThat(viewSortType.get()).isEqualTo(ProfileSortType.VIEW);

        assertThat(newestSortType).isPresent();
        assertThat(newestSortType.get()).isEqualTo(ProfileSortType.NEWEST);
    }

    @DisplayName("유효하지 않은 paramName으로 from 메서드 호출 시 Optional.empty()를 반환한다.")
    @Test
    void from_withInvalidParamName() {
        // given
        String invalidParam = "invalidSort";
        String emptyParam = "";
        String nullParam = null;

        // when
        Optional<ProfileSortType> resultInvalid = ProfileSortType.from(invalidParam);
        Optional<ProfileSortType> resultEmpty = ProfileSortType.from(emptyParam);
        Optional<ProfileSortType> resultNull = ProfileSortType.from(nullParam);

        // then
        assertThat(resultInvalid).isEmpty();
        assertThat(resultEmpty).isEmpty();
        assertThat(resultNull).isEmpty();
    }

    @DisplayName("getDefault 메서드 호출 시 기본 정렬 타입인 NEWEST를 반환한다.")
    @Test
    void getDefault_returnsNewest() {
        // when
        ProfileSortType defaultSortType = ProfileSortType.getDefault();

        // then
        assertThat(defaultSortType).isEqualTo(ProfileSortType.NEWEST);
    }

}
