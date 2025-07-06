package com.myrealstore.profile.controller;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myrealstore.global.common.ApiResponse;
import com.myrealstore.profile.controller.request.ProfileSearchRequest;
import com.myrealstore.profile.service.ProfileService;
import com.myrealstore.profile.service.response.ProfileResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public ApiResponse<Page<ProfileResponse>> getProfiles(
            @Valid @ModelAttribute ProfileSearchRequest request) {

        Page<ProfileResponse> profiles = profileService.getProfiles(request.toServiceRequest());
        return ApiResponse.ok(profiles);
    }


    @GetMapping("/{id}")
    public ApiResponse<ProfileResponse> getProfile(@PathVariable Long id) {
        ProfileResponse response = profileService.getProfileAndIncreaseView(id);
        return ApiResponse.ok(response);
    }
}
