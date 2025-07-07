package com.myrealstore.payment.client;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.myrealstore.payment.properties.PaymentProperties;
import com.myrealstore.payment.service.request.PaymentApprovalServiceRequest;
import com.myrealstore.payment.service.request.PaymentCancelServiceRequest;
import com.myrealstore.payment.service.response.PaymentApprovalResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service("toss")
public class TossPaymentClient implements PaymentClient {

    private final RestClient.Builder restClientBuilder;
    private final PaymentProperties paymentProperties;

    @Override
    public PaymentApprovalResponse requestPaymentApproval(PaymentApprovalServiceRequest request) {
        String baseUrl = paymentProperties.getToss().getBaseUrl();
        String secretKey = paymentProperties.getToss().getSecretKey();

        return restClientBuilder.build()
                                .post()
                                .uri(baseUrl + "/payments/confirm" )
                                .header("Authorization",
                                        "Basic " + encodeSecretKey(secretKey))
                                .body(request)
                                .retrieve()
                                .body(PaymentApprovalResponse.class);
    }

    @Override
    public void requestPaymentCancel(PaymentCancelServiceRequest request) {
        String baseUrl = paymentProperties.getToss().getBaseUrl();
        String secretKey = paymentProperties.getToss().getSecretKey();

        restClientBuilder.build()
                                .post()
                                .uri(baseUrl + "/payments/" + request.getPaymentKey() + "cancel" )
                                .header("Authorization",
                                        "Basic " + encodeSecretKey(secretKey))
                                .body(request)
                                .retrieve()
                                .body(PaymentApprovalResponse.class);
    }

    private String encodeSecretKey(String secretKey) {
        return Base64.getEncoder()
                     .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
    }
}
