package com.myrealstore.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.myrealstore.payment.client.PaymentClient;
import com.myrealstore.payment.exception.PaymentProcessingException;
import com.myrealstore.payment.exception.PointChargeException;
import com.myrealstore.payment.service.request.PaymentApprovalServiceRequest;
import com.myrealstore.payment.service.response.PaymentApprovalResponse;
import com.myrealstore.point.service.PointService;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    public static final String TOSS = "toss";

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PointService pointService;
    @Mock
    private PaymentClient paymentClient;

    private PaymentApprovalServiceRequest request;

    @BeforeEach
    void setUp() {
        Map<String, PaymentClient> clientMap = Map.of(TOSS, paymentClient);
        paymentService = new PaymentService(clientMap, pointService);

        request = PaymentApprovalServiceRequest.builder()
                                               .paymentKey("test-payment-key")
                                               .orderId("order-123")
                                               .amount(1000)
                                               .memberId(1L)
                                               .provider(TOSS)
                                               .build();

        given(paymentClient.requestPaymentApproval(any()))
                .willReturn(PaymentApprovalResponse.builder()
                                                   .paymentKey("test-payment-key")
                                                   .orderId("order-123")
                                                   .totalAmount(1000)
                                                   .status("DONE")
                                                   .build());
    }

    @DisplayName("성공적으로 결제 승인 및 포인트 적립이 완료된다")
    @Test
    void success_paymentApprovalAndPointCharge() {
        // when
        PaymentApprovalResponse result = paymentService.requestApproval(request);

        // then
        assertThat(result.getPaymentKey()).isEqualTo("test-payment-key");
        assertThat(result.getOrderId()).isEqualTo("order-123");
        assertThat(result.getStatus()).isEqualTo("DONE");

        verify(paymentClient).requestPaymentApproval(any());
        verify(pointService).chargePoint(argThat(req ->
                                                         req.getMemberId().equals(1L) &&
                                                         req.getAmount() == 1000 &&
                                                         req.getReason().equals(TOSS)
        ));
    }

    @DisplayName("포인트 적립 실패 시 결제 취소가 시도되고 예외가 발생한다")
    @Test
    void pointChargeFail_triggersRefundAndThrows() {
        // given
        doThrow(new PointChargeException("포인트 적립 실패"))
                .when(pointService)
                .chargePoint(any());

        willDoNothing()
                .given(paymentClient)
                .requestPaymentCancel(any());

        // when & then
        assertThatThrownBy(() -> paymentService.requestApproval(request))
                .isInstanceOf(PaymentProcessingException.class)
                .hasMessage("결제 취소 시도 실패");

        verify(paymentClient).requestPaymentCancel(argThat(cancel ->
                                                                   cancel.getPaymentKey()
                                                                         .equals("test-payment-key")));
    }

    @DisplayName("결제 승인 실패 시 예외가 발생한다")
    @Test
    void paymentApprovalFail_throwsException() {
        // given
        given(paymentClient.requestPaymentApproval(any()))
                .willReturn(PaymentApprovalResponse.builder()
                                                   .paymentKey("test-payment-key")
                                                   .orderId("order-123")
                                                   .totalAmount(1000)
                                                   .status("FAILED")
                                                   .build());

        // when & then
        assertThatThrownBy(() -> paymentService.requestApproval(request))
                .isInstanceOf(PaymentProcessingException.class)
                .hasMessage("결제 승인 실패");

        verify(pointService, never()).chargePoint(any());
    }

    @DisplayName("결제사 호출 자체가 실패하면 런타임 예외 발생")
    @Test
    void paymentApiCallThrows_exceptionThrown() {
        // given
        given(paymentClient.requestPaymentApproval(any()))
                .willThrow(new RuntimeException("외부 API 에러"));

        // when & then
        assertThatThrownBy(() -> paymentService.requestApproval(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("결제사 승인 요청 중 오류 발생");

        verify(pointService, never()).chargePoint(any());
    }

}
