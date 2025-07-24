package com.myrealstore.payment.client;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.myrealstore.global.common.error.ErrorCode;
import com.myrealstore.global.common.exception.BusinessException;
import com.myrealstore.payment.properties.PaymentProperties;
import com.myrealstore.payment.service.request.PaymentConfirmServiceRequest;
import com.myrealstore.payment.service.response.PaymentConfirmResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service("toss")
public class TossPaymentClient implements PaymentClient {

    private final RestClient.Builder restClientBuilder;
    private final PaymentProperties paymentProperties;

    @Override
    public PaymentConfirmResponse requestConfirm(PaymentConfirmServiceRequest request) {
        String baseUrl = paymentProperties.getToss().getBaseUrl();
        String secretKey = paymentProperties.getToss().getSecretKey();

        return restClientBuilder.build()
                                .post()
                                .uri(baseUrl + "/payments/confirm")
                                .header("Authorization",
                                        "Basic " + encodeSecretKey(secretKey))
                                .body(request)
                                .retrieve()
                                .onStatus(HttpStatusCode::isError, (status, response) -> {
                                    throw new BusinessException("결제 확인 실패",
                                                                ErrorCode.PAYMENT_PROCESSING_FAILED);
                                })
                                .body(PaymentConfirmResponse.class);
    }

    @Override
    public PaymentConfirmResponse requestConfirmAlwaysReturnOk(PaymentConfirmServiceRequest request) {
        return PaymentConfirmResponse.builder()
                                     .orderId(request.getOrderId())
                                     .paymentKey(request.getPaymentKey())
                                     .finalAmount(request.getAmount())
                                     .build();
    }

    private String encodeSecretKey(String secretKey) {
        return Base64.getEncoder()
                     .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
    }
}
