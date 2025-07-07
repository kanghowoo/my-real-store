package com.myrealstore.payment.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myrealstore.global.common.ApiResponse;
import com.myrealstore.payment.controller.request.PaymentApprovalRequest;
import com.myrealstore.payment.service.PaymentService;
import com.myrealstore.payment.service.response.PaymentApprovalResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/approval")
    public ApiResponse<PaymentApprovalResponse> requestApproval(
            @RequestBody @Valid PaymentApprovalRequest request
    ) {
        PaymentApprovalResponse response = paymentService.requestApproval(request.toServiceRequest());
        return ApiResponse.ok(response);
    }

}



