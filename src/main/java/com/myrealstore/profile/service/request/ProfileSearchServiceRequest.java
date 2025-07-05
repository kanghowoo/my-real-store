package com.myrealstore.profile.service.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ProfileSearchServiceRequest {
    private final int page;
    private final int size;
    private final String sort;

    @Builder
    public ProfileSearchServiceRequest(int page, int size, String sort) {
        this.page = page;
        this.size = size;
        this.sort = sort;
    }
}
