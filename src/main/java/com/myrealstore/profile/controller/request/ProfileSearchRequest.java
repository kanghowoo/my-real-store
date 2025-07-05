package com.myrealstore.profile.controller.request;

import com.myrealstore.profile.service.request.ProfileSearchServiceRequest;

import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProfileSearchRequest {
    @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다.")
    private int page = 0;

    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
    private int size = 10;

    String sort = "";

    @Builder
    public ProfileSearchRequest(int page, int size, String sort) {
        this.page = page;
        this.size = size;
        this.sort = sort;
    }

    public ProfileSearchServiceRequest toServiceRequest() {
        return ProfileSearchServiceRequest.builder()
                                          .page(page)
                                          .size(size)
                                          .sort(sort)
                                          .build();
    }
}
