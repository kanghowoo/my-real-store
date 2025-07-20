package com.myrealstore.point.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myrealstore.global.common.ApiResponse;
import com.myrealstore.point.controller.request.PointChargeRequest;
import com.myrealstore.point.service.PointService;
import com.myrealstore.point.service.response.PointChargeResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    @PostMapping("/charge")
    public ApiResponse<PointChargeResponse> chargePoint(
            @RequestBody @Valid PointChargeRequest request
    ) {
        PointChargeResponse response = pointService.chargePoint(request.toServiceRequest());
        return ApiResponse.ok(response);
    }

    @PostMapping("/use")
    public ApiResponse<String> usePoint(
            @RequestBody @Valid PointChargeRequest request
    ) {
        pointService.usePoint(request.toServiceRequest());
        return ApiResponse.ok("SUCCESS");
    }
}
