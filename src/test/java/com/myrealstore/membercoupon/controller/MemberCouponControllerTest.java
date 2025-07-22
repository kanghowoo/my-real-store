package com.myrealstore.membercoupon.controller;

import static com.myrealstore.coupon.domain.DiscountType.FIXED;
import static com.myrealstore.coupon.domain.DiscountType.PERCENT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.myrealstore.coupon.domain.Coupon;
import com.myrealstore.coupon.domain.DiscountType;
import com.myrealstore.member.domain.Member;
import com.myrealstore.membercoupon.domain.MemberCoupon;
import com.myrealstore.membercoupon.service.MemberCouponService;
import com.myrealstore.membercoupon.service.response.MemberCouponResponse;

@WebMvcTest(MemberCouponController.class)
class MemberCouponControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberCouponService memberCouponService;

    @Test
    @DisplayName("사용 가능한 쿠폰 목록 조회")
    void getAvailableCoupons_shouldReturnCouponList() throws Exception {
        // given
        Long memberId = 1L;
        Coupon coupon1 =
                createCoupon("쿠폰1", FIXED, 2000, 0, LocalDateTime.now().plusDays(3), true);
        Coupon coupon2 =
                createCoupon("쿠폰2", PERCENT, 10, 5000, LocalDateTime.now().plusDays(3), true);

        MemberCoupon memberCoupon1 = createMemberCoupon(coupon1, false);
        MemberCoupon memberCoupon2 = createMemberCoupon(coupon2, false);

        List<MemberCouponResponse> mockCoupons = List.of(
                MemberCouponResponse.of(memberCoupon1),
                MemberCouponResponse.of(memberCoupon2)
        );

        given(memberCouponService.getAvailableCoupons(eq(memberId), any()))
                .willReturn(mockCoupons);

        // when & then
        mockMvc.perform(get("/api/member-coupons/{memberId}/available", memberId)
                                .accept(APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.message").value("OK"))
               .andExpect(jsonPath("$.data").isArray())
               .andExpect(jsonPath("$.data[0].name").value("쿠폰1"))
               .andExpect(jsonPath("$.data[1].discountType").value("PERCENT"));
    }

    private Member createMember(String name) {
        return Member.builder().name(name).build();
    }

    private Coupon createCoupon(String name, DiscountType type, int discountValue, int maxAmount,
                                LocalDateTime expiredAt, boolean enabled) {
        return Coupon.builder()
                     .name(name)
                     .discountType(type)
                     .discountValue(discountValue)
                     .maxAmount(maxAmount)
                     .expiredAt(expiredAt)
                     .enabled(enabled)
                     .build();
    }

    private MemberCoupon createMemberCoupon(Coupon coupon, boolean used) {
        return MemberCoupon.builder()
                           .member(createMember("테스트 회원"))
                           .coupon(coupon)
                           .used(used)
                           .build();
    }

}
