package com.myrealstore.payment.client;

import com.myrealstore.payment.service.request.PaymentConfirmServiceRequest;
import com.myrealstore.payment.service.response.PaymentConfirmResponse;

public interface PaymentClient {
    PaymentConfirmResponse requestConfirm(PaymentConfirmServiceRequest request);

    PaymentConfirmResponse requestConfirmAlwaysReturnOk(PaymentConfirmServiceRequest request);
}
