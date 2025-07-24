package com.myrealstore.global.common.error;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {
    INVALID_INPUT_VALUE("invalid input value", BAD_REQUEST),
    ENTITY_NOT_FOUND("entity not found", NOT_FOUND),

    POINT_CHARGE_FAILED("point charge request failed.", BAD_REQUEST),
    PAYMENT_PROCESSING_FAILED("payment event request failed.", BAD_REQUEST),

    ALREADY_USED_COUPON("이미 사용된 쿠폰입니다.", CONFLICT),
    DUPLICATED_PAYMENT_REQUEST("중복 결제 요청 입니다.", CONFLICT);

    private final String message;
    private final HttpStatus status;

    ErrorCode(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
