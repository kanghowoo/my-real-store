package com.myrealstore.payment.exception;

import com.myrealstore.global.common.error.ErrorCode;
import com.myrealstore.global.common.exception.BusinessException;

public class PaymentProcessingException extends BusinessException {

    public PaymentProcessingException(String message) {
        super(message, ErrorCode.PAYMENT_PROCESSING_FAILED);
    }
}
