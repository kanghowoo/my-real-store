package com.myrealstore.payment.client;

import com.myrealstore.payment.service.request.PaymentApprovalServiceRequest;
import com.myrealstore.payment.service.request.PaymentCancelServiceRequest;
import com.myrealstore.payment.service.response.PaymentApprovalResponse;

public interface PaymentClient {
    PaymentApprovalResponse requestPaymentApproval(PaymentApprovalServiceRequest request);
    void requestPaymentCancel(PaymentCancelServiceRequest request);

}
