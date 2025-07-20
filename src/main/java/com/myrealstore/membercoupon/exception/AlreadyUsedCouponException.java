package com.myrealstore.membercoupon.exception;

import com.myrealstore.global.common.error.ErrorCode;
import com.myrealstore.global.common.exception.BusinessException;

public class AlreadyUsedCouponException extends BusinessException {
    public AlreadyUsedCouponException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public AlreadyUsedCouponException(String message) {
        super(message, ErrorCode.ALREADY_USED_COUPON);
    }

    public AlreadyUsedCouponException() {
        super(ErrorCode.ALREADY_USED_COUPON.getMessage(), ErrorCode.ALREADY_USED_COUPON);
    }
}
