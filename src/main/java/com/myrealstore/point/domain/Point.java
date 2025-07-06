package com.myrealstore.point.domain;

import com.myrealstore.global.common.BaseEntity;
import com.myrealstore.member.domain.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Point extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // + 또는 -
    @Column(nullable = false)
    private int amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PointEventType type;

    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public static Point createCharge(Member member, int amount, String reason) {
        return Point.builder()
                    .member(member)
                    .amount(amount)
                    .type(PointEventType.CHARGE)
                    .reason(reason)
                    .build();
    }

    public static Point createUse(Member member, int amount, String reason) {
        return Point.builder()
                    .member(member)
                    .amount(amount)
                    .type(PointEventType.USE)
                    .reason(reason)
                    .build();
    }
}
