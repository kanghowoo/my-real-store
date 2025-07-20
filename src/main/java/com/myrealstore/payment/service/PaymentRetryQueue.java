package com.myrealstore.payment.service;

import com.myrealstore.payment.service.request.PaymentConfirmServiceRequest;

public interface PaymentRetryQueue {
    void retryConfirmPayment(PaymentConfirmServiceRequest request, long delayInMillis);
}
