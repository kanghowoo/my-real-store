package com.myrealstore.point.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myrealstore.point.controller.request.PointChargeRequest;
import com.myrealstore.point.service.PointService;

@WebMvcTest(controllers = PointController.class)
class PointControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    PointService pointService;

    @Test
    @DisplayName("포인트 충전 성공 요청")
    void chargePoint_success() throws Exception {
        PointChargeRequest request = PointChargeRequest.builder()
                                                       .memberId(1L)
                                                       .amount(1000)
                                                       .reason("test")
                                                       .build();

        mockMvc.perform(post("/api/points/charge")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.message").value("OK"))
               .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("포인트 사용 성공 요청")
    void usePoint_success() throws Exception {
        PointChargeRequest request = PointChargeRequest.builder()
                                                       .memberId(1L)
                                                       .amount(1000)
                                                       .reason("test")
                                                       .build();

        mockMvc.perform(post("/api/points/use")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.message").value("OK"))
               .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("유효성 실패: 충전 금액 0원")
    void chargePoint_fail_invalidAmount() throws Exception {
        PointChargeRequest request = PointChargeRequest.builder()
                                                       .memberId(1L)
                                                       .amount(0)
                                                       .reason("test")
                                                       .build();

        mockMvc.perform(post("/api/points/charge")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.message").value("충전 금액은 1 이상이어야 합니다."))
               .andExpect(jsonPath("$.statusCode").value(HttpStatus.BAD_REQUEST.value()));
    }

}
