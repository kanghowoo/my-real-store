package com.myrealstore.global.common.error;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import org.springframework.core.codec.ByteArrayDecoder;
import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {
    INVALID_INPUT_VALUE("invalid input value", BAD_REQUEST),
    ENTITY_NOT_FOUND("entity not found", NOT_FOUND),

    POINT_CHARGE_FAILED("point charge request failed.", BAD_REQUEST),
    PAYMENT_PROCESSING_FAILED("payment event request failed.",BAD_REQUEST)
    ;

    private final String message;
    private final HttpStatus status;

    ErrorCode(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
