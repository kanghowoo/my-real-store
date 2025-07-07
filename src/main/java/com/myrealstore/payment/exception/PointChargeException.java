package com.myrealstore.payment.exception;

import com.myrealstore.global.common.error.ErrorCode;
import com.myrealstore.global.common.exception.BusinessException;

public class PointChargeException extends BusinessException {
    public PointChargeException(String message) {
        super(message, ErrorCode.POINT_CHARGE_FAILED);
    }
}
