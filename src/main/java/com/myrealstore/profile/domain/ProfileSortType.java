package com.myrealstore.profile.domain;

import static com.myrealstore.profile.domain.QProfile.profile;

import java.util.Arrays;
import java.util.Optional;

import com.querydsl.core.types.OrderSpecifier;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ProfileSortType {
    NAME("name", "이름순") {
        @Override
        public OrderSpecifier<?> getOrderSpecifier() {
            return profile.name.asc();
        }
    }, VIEW("view", "조회순") {
        @Override
        public OrderSpecifier<?> getOrderSpecifier() {
            return profile.viewCount.desc();
        }
    }, NEWEST("newest", "최신순") {
        @Override
        public OrderSpecifier<?> getOrderSpecifier() {
            return profile.createdAt.desc();
        }
    };

    private final String paramName;
    private final String description;

    public abstract OrderSpecifier<?> getOrderSpecifier();

    public static Optional<ProfileSortType> from(String paramName) {
        return Optional.ofNullable(paramName)
                       .map(String::trim)
                       .filter(s -> !s.isEmpty())
                       .flatMap(trimmedParam -> Arrays.stream(ProfileSortType.values())
                                                      .filter(type -> type.paramName.equalsIgnoreCase(
                                                              trimmedParam))
                                                      .findFirst());
    }

    public static ProfileSortType getDefault() {
        return NEWEST;
    }
}
