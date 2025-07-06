package com.myrealstore.profile.domain;

import com.myrealstore.global.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Profile extends BaseEntity {

    public static final int DEFAULT_VIEW_COUNT = 0;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "view_count", nullable = false)
    private int viewCount;

    public static Profile create(String name) {
        return Profile.builder()
                .name(name)
                .viewCount(DEFAULT_VIEW_COUNT)
                .build();
    }

    public void increaseView() {
        this.viewCount += 1;
    }
}
