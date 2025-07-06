package com.myrealstore.profile.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.myrealstore.global.common.exception.EntityNotFoundException;
import com.myrealstore.profile.service.ProfileService;
import com.myrealstore.profile.service.request.ProfileSearchServiceRequest;
import com.myrealstore.profile.service.response.ProfileResponse;

@WebMvcTest(controllers = ProfileController.class)
class ProfileControllerTest {

    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final int DEFAULT_PAGE_SIZE = 10;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProfileService profileService;

    @DisplayName("프로필 목록을 주어진 정렬 값에 따라 페이지 단위로 조회한다.")
    @Test
    void getProfiles() throws Exception {
        // given
        ProfileResponse profileResponse1 = ProfileResponse.builder()
                                                          .id(0L)
                                                          .name("김철수")
                                                          .viewCount(3)
                                                          .createdAt(LocalDateTime.now())
                                                          .build();

        ProfileResponse profileResponse2 = ProfileResponse.builder()
                                                          .id(1L)
                                                          .name("이영희")
                                                          .viewCount(1)
                                                          .createdAt(LocalDateTime.now())
                                                          .build();

        PageImpl<ProfileResponse> responsePage = new PageImpl<>(
                List.of(profileResponse1, profileResponse2),
                PageRequest.of(DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE),
                2);

        String sortParam = "VIEW";

        given(profileService.getProfiles(any(ProfileSearchServiceRequest.class))).willReturn(responsePage);

        // when & then
        mockMvc.perform(
                       get("/api/profiles")
                               .param("page", String.valueOf(DEFAULT_PAGE_NUMBER))
                               .param("size", String.valueOf(DEFAULT_PAGE_SIZE))
                               .param("sort", sortParam)
               )
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.code").value("200"))
               .andExpect(jsonPath("$.status").value("OK"))
               .andExpect(jsonPath("$.message").value("OK"))
               .andExpect(jsonPath("$.data.content[0].name", is("김철수")))
               .andExpect(jsonPath("$.data.content[0].viewCount", is(3)))
               .andExpect(jsonPath("$.data.content[1].name", is("이영희")))
               .andExpect(jsonPath("$.data.content[1].viewCount", is(1)));
    }

    @DisplayName("목록조회를 위한 page 파라미터 값은 양수이어야 한다.")
    @Test
    void invalidPage() throws Exception {
        // given
        String invalidPage = "-1"; // 음수 페이지 번호

        // when & then
        mockMvc.perform(
                       get("/api/profiles")
                               .param("page", invalidPage)
                               .param("size", String.valueOf(DEFAULT_PAGE_SIZE))
               )
               .andDo(print())
               .andExpect(status().isBadRequest()) // 400 Bad Request
               .andExpect(jsonPath("$.statusCode").value(400))
               .andExpect(jsonPath("$.message").value("페이지 번호는 0 이상이어야 합니다."));
    }

    @DisplayName("목록조회를 위한 size 파라미터 값은 1보다 커야 한다.")
    @Test
    void invalidPageSize() throws Exception {
        // given
        String invalidPageSize = "0";

        // when & then
        mockMvc.perform(
                       get("/api/profiles")
                               .param("page", String.valueOf(DEFAULT_PAGE_NUMBER))
                               .param("size", invalidPageSize)
               )
               .andDo(print())
               .andExpect(status().isBadRequest()) // 400 Bad Request
               .andExpect(jsonPath("$.statusCode").value(400))
               .andExpect(jsonPath("$.message").value("페이지 크기는 1 이상이어야 합니다."));
    }

    @Test
    @DisplayName("정상적인 ID로 프로필 상세 조회 시 200과 데이터를 반환한다.")
    void getProfileById_success() throws Exception {
        // given
        Long id = 1L;
        ProfileResponse response = ProfileResponse.builder()
                                                  .id(id)
                                                  .name("홍길동")
                                                  .viewCount(5)
                                                  .createdAt(LocalDateTime.now())
                                                  .build();

        given(profileService.getProfileAndIncreaseView(id)).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/profiles/{id}", id))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.data.id").value(id))
               .andExpect(jsonPath("$.data.name").value("홍길동"))
               .andExpect(jsonPath("$.data.viewCount").value(5));
    }

    @Test
    @DisplayName("존재하지 않는 ID 요청 시 404 응답")
    void getProfileById_notFound() throws Exception {
        // given
        Long invalidId = 999L;

        given(profileService.getProfileAndIncreaseView(invalidId))
                .willThrow(new EntityNotFoundException("해당 프로필을 찾을 수 없습니다."));

        // when & then
        mockMvc.perform(get("/api/profiles/{id}", invalidId))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.message").value("entity not found"))
               .andExpect(jsonPath("$.statusCode").value(404));
    }

}
