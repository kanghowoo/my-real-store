package com.myrealstore.member.service.response;

import java.time.LocalDateTime;

import com.myrealstore.member.domain.Member;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberResponse {
    private Long id;
    private String name;
    private int point;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public MemberResponse(Long id, String name, int point, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.point = point;
    }

    public static MemberResponse of(Member member) {
        return MemberResponse.builder()
                             .id(member.getId())
                             .name(member.getName())
                             .point(member.getPoint())
                             .createdAt(member.getCreatedAt())
                             .updatedAt(member.getUpdatedAt())
                             .build();
    }
}
