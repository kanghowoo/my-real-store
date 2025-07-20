package com.myrealstore.payment.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myrealstore.global.common.ApiResponse;
import com.myrealstore.payment.controller.request.PaymentConfirmRequest;
import com.myrealstore.payment.service.PaymentService;
import com.myrealstore.payment.service.response.PaymentConfirmResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/confirm")
    public ApiResponse<PaymentConfirmResponse> requestConfirm(
            @RequestBody @Valid PaymentConfirmRequest request
    ) {
        PaymentConfirmResponse response = paymentService.confirmPayment(request.toServiceRequest());
        return ApiResponse.ok(response);
    }

    @PostMapping("/confirm/ok")
    public ApiResponse<PaymentConfirmResponse> requestConfirmAlwaysReturnOk(
            @RequestBody @Valid PaymentConfirmRequest request
    ) {
        PaymentConfirmResponse response = paymentService.confirmPaymentAlwaysReturnOk(request.toServiceRequest());
        return ApiResponse.ok(response);
    }

}



