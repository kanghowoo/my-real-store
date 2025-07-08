package com.myrealstore.payment.service.response;

import org.springframework.stereotype.Service;

import com.myrealstore.payment.service.PaymentRetryQueue;
import com.myrealstore.payment.service.request.PaymentCancelServiceRequest;

@Service
public class SqsPaymentRetryQueue implements PaymentRetryQueue {

    @Override
    public void retryPaymentCancel(PaymentCancelServiceRequest request, long delayInMillis) {
        // delay 이후 AWS SQS에 Enqueue 하고 원활한 결제 취소 재시도를 위해 나중에 retry 한다.
    }
}
