package com.myrealstore.membercoupon.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myrealstore.global.common.ApiResponse;
import com.myrealstore.membercoupon.controller.request.ApplyCouponRequest;
import com.myrealstore.membercoupon.service.MemberCouponService;
import com.myrealstore.membercoupon.service.response.ApplyCouponResponse;
import com.myrealstore.membercoupon.service.response.MemberCouponResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member-coupons")
public class MemberCouponController {

    private final MemberCouponService memberCouponService;

    @GetMapping("/{memberId}/available")
    public ApiResponse<List<MemberCouponResponse>> getAvailableCoupons(
            @PathVariable("memberId") Long memberId
    ) {
        List<MemberCouponResponse> coupons =
                memberCouponService.getAvailableCoupons(memberId, LocalDateTime.now());
        return ApiResponse.ok(coupons);
    }

    @PostMapping("/apply")
    public ApiResponse<ApplyCouponResponse> applyCoupon(@RequestBody ApplyCouponRequest request) {
        ApplyCouponResponse response = memberCouponService.applyCoupon(request.toServiceRequest());
        return ApiResponse.ok(response);
    }

}
