package com.myrealstore.payment.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PaymentStatus {
    READY("준비"),
    DONE("완료"),
    FAILED("실패"),
    CANCELED("취소");

    private final String description;
}
