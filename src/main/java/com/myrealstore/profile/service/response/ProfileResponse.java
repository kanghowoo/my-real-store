package com.myrealstore.profile.service.response;

import java.time.LocalDateTime;

import com.myrealstore.profile.domain.Profile;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ProfileResponse {
    private final Long id;
    private final String name;
    private final int viewCount;
    private final LocalDateTime createdAt;

    @Builder
    public ProfileResponse(Long id, String name, int viewCount, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.viewCount = viewCount;
        this.createdAt = createdAt;
    }

    public static ProfileResponse of(Profile profile) {
        return ProfileResponse.builder()
                              .id(profile.getId())
                              .name(profile.getName())
                              .viewCount(profile.getViewCount())
                              .build();

    }
}
