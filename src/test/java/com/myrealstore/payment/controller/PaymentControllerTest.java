package com.myrealstore.payment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myrealstore.payment.controller.request.PaymentApprovalRequest;
import com.myrealstore.payment.service.PaymentService;
import com.myrealstore.payment.service.response.PaymentApprovalResponse;

@WebMvcTest(controllers = PaymentController.class)
class PaymentControllerTest {

    public static final String baseUrl = "/api/payments/approval";
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("결제 승인 요청을 처리하고 응답을 반환한다")
    @Test
    void requestApproval_success() throws Exception {
        // given
        PaymentApprovalRequest request = PaymentApprovalRequest.builder()
                                                               .memberId(1L)
                                                               .paymentKey("test-key")
                                                               .orderId("5678")
                                                               .amount(1000)
                                                               .provider("toss")
                                                               .build();

        PaymentApprovalResponse mockResponse = PaymentApprovalResponse.builder()
                                                                      .paymentKey("test-key")
                                                                      .orderId("5678")
                                                                      .totalAmount(1000)
                                                                      .build();

        given(paymentService.requestApproval(any())).willReturn(mockResponse);

        // when & then
        mockMvc.perform(post(baseUrl)
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.data.paymentKey").value("test-key"))
               .andExpect(jsonPath("$.data.orderId").value("5678"))
               .andExpect(jsonPath("$.data.totalAmount").value(1000));
    }

    @Test
    @DisplayName("회원 ID가 null이면 400반환")
    void requestPaymentApproval_memberId_is_null_returnsBadRequest() throws Exception {
        // given
        PaymentApprovalRequest request = PaymentApprovalRequest.builder()
                                                               .memberId(null)
                                                               .paymentKey("pay_1234")
                                                               .orderId("order_5678")
                                                               .amount(1000)
                                                               .provider("toss")
                                                               .build();

        // when & then
        mockMvc.perform(post(baseUrl)
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.statusCode").value(400))
               .andExpect(jsonPath("$.message").value("회원 ID는 필수입니다."));
    }

    @Test
    @DisplayName("결제 키가 빈 문자열이면 400반환")
    void requestPaymentApproval_paymentKey_is_null_returnsBadRequest() throws Exception {
        PaymentApprovalRequest request = PaymentApprovalRequest.builder()
                                                               .memberId(1L)
                                                               .paymentKey("")
                                                               .orderId("order_5678")
                                                               .amount(1000)
                                                               .provider("toss")
                                                               .build();

        mockMvc.perform(post(baseUrl)
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.statusCode").value(400))
               .andExpect(jsonPath("$.message").value("결제 키는 필수입니다."));
    }

    @Test
    @DisplayName("주문 ID가 null이면 400반환")
    void requestPaymentApproval_orderId_is_null_returnsBadRequest() throws Exception {
        // given
        PaymentApprovalRequest request = PaymentApprovalRequest.builder()
                                                               .memberId(1L)
                                                               .paymentKey("pay_1234")
                                                               .orderId(null)
                                                               .amount(1000)
                                                               .provider("toss")
                                                               .build();

        // when & then
        mockMvc.perform(post(baseUrl)
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.statusCode").value(400))
               .andExpect(jsonPath("$.message").value("주문 ID는 필수입니다."));
    }

    @Test
    @DisplayName("amount가 0이면 400 반환")
    void requestPaymentApproval_invalidAmount_returnsBadRequest() throws Exception {
        // given
        PaymentApprovalRequest request = PaymentApprovalRequest.builder()
                                                               .memberId(1L)
                                                               .paymentKey("pay_1234")
                                                               .orderId("order_5678")
                                                               .amount(0)
                                                               .provider("toss")
                                                               .build();

        // when & then
        mockMvc.perform(post(baseUrl)
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.statusCode").value(400))
               .andExpect(jsonPath("$.message").value("충전 금액은 1원 이상이어야 합니다."));
    }

    @Test
    @DisplayName("provider가 없으면 400 반환")
    void requestPaymentApproval_missingProvider_returnsBadRequest() throws Exception {
        // given
        PaymentApprovalRequest request = PaymentApprovalRequest.builder()
                                                               .memberId(1L)
                                                               .paymentKey("pay_1234")
                                                               .orderId("order_5678")
                                                               .amount(1000)
                                                               .provider(null)
                                                               .build();

        // when & then
        mockMvc.perform(post(baseUrl)
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.statusCode").value(400))
               .andExpect(jsonPath("$.message").value("결제 시스템 제공자는 필수입니다."));
    }

}
