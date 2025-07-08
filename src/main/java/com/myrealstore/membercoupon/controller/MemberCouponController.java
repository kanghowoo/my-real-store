package com.myrealstore.membercoupon.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.myrealstore.global.common.ApiResponse;
import com.myrealstore.membercoupon.service.MemberCouponService;
import com.myrealstore.membercoupon.service.response.MemberCouponResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member-coupons")
public class MemberCouponController {

    private final MemberCouponService memberCouponService;

    @GetMapping("/available")
    public ApiResponse<List<MemberCouponResponse>> getAvailableCoupons(
            @RequestParam("memberId") Long memberId
    ) {
        List<MemberCouponResponse> coupons =
                memberCouponService.getAvailableCoupons(memberId, LocalDateTime.now());
        return ApiResponse.ok(coupons);
    }

    @PostMapping("/{memberCouponId}/use")
    public ApiResponse<MemberCouponResponse> useCoupon(@PathVariable Long memberCouponId) {
        MemberCouponResponse response = memberCouponService.useCoupon(memberCouponId);
        return ApiResponse.ok(response);
    }



}
