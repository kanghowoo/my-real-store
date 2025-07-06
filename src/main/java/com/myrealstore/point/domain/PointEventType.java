package com.myrealstore.point.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PointEventType {
    CHARGE("충전"),
    USE("사용");

    private final String description;
}
