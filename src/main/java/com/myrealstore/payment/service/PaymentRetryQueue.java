package com.myrealstore.payment.service;

import com.myrealstore.payment.service.request.PaymentCancelServiceRequest;

public interface PaymentRetryQueue {
    void retryPaymentCancel(PaymentCancelServiceRequest request, long delayInMillis);
}
