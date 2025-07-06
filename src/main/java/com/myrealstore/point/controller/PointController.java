package com.myrealstore.point.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myrealstore.global.common.ApiResponse;
import com.myrealstore.point.controller.request.PointEventRequest;
import com.myrealstore.point.service.PointService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    @PostMapping("/charge")
    public ApiResponse<String> chargePoint(
            @RequestBody @Valid PointEventRequest request
    ) {
        pointService.chargePoint(request.toServiceRequest());
        return ApiResponse.ok("SUCCESS");
    }

    @PostMapping("/use")
    public ApiResponse<String> usePoint(
            @RequestBody @Valid PointEventRequest request
    ) {
        pointService.usePoint(request.toServiceRequest());
        return ApiResponse.ok("SUCCESS");
    }
}
