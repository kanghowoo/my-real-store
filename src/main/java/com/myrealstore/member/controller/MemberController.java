package com.myrealstore.member.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myrealstore.global.common.ApiResponse;
import com.myrealstore.member.service.MemberService;
import com.myrealstore.member.service.response.MemberResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/{id}")
    public ApiResponse<MemberResponse> getProfile(@PathVariable("id") Long id) {
        MemberResponse response = memberService.getMember(id);
        return ApiResponse.ok(response);
    }
}
