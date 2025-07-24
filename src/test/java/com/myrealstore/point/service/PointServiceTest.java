package com.myrealstore.point.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.myrealstore.member.domain.Member;
import com.myrealstore.member.repository.MemberRepository;
import com.myrealstore.point.domain.Point;
import com.myrealstore.point.repository.PointRepository;
import com.myrealstore.point.service.request.PointChargeServiceRequest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class PointServiceTest {

    @Autowired
    PointService pointService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PointRepository pointRepository;

    @PersistenceContext
    private EntityManager em;

    @DisplayName("결제 금액을 기준으로 회원에게 포인트를 충전한다.")
    @Test
    void chargePoints() {
        // given
        int chargeAmount = 1000;
        Member member = memberRepository.save(Member.builder()
                                                    .name("홍길동")
                                                    .point(0)
                                                    .build());

        PointChargeServiceRequest request = PointChargeServiceRequest.builder()
                                                                     .memberId(member.getId())
                                                                     .memberCouponId(null)
                                                                     .amount(chargeAmount)
                                                                     .reason("test")
                                                                     .build();
        // when
        pointService.chargePoint(request);
        em.flush();
        em.clear();

        // then
        Member updatedMember = memberRepository.findById(member.getId()).orElseThrow();
        assertThat(updatedMember.getPoint()).isEqualTo(chargeAmount);

        List<Point> points = pointRepository.findAll();
        assertThat(points).hasSize(1);
        assertThat(points.get(0).getAmount()).isEqualTo(chargeAmount);
        assertThat(points.get(0).getType().name()).isEqualTo("CHARGE");
    }

    @DisplayName("포인트 사용 시 회원 포인트가 감소한다.")
    @Test
    void usePoints() {
        // given
        int useAmount = 1000;
        int initAmount = 1000;
        Member member = memberRepository.save(Member.builder()
                                                    .name("홍길동")
                                                    .point(initAmount)
                                                    .build());

        PointChargeServiceRequest request = PointChargeServiceRequest.builder()
                                                                     .memberId(member.getId())
                                                                     .amount(useAmount)
                                                                     .reason("test")
                                                                     .build();

        // when
        pointService.usePoint(request);
        em.flush();
        em.clear();

        // then
        Member updatedMember = memberRepository.findById(member.getId()).orElseThrow();
        assertThat(updatedMember.getPoint()).isEqualTo(initAmount - useAmount);

        List<Point> points = pointRepository.findAll();
        assertThat(points).hasSize(1);
        assertThat(points.get(0).getAmount()).isEqualTo(useAmount);
        assertThat(points.get(0).getType().name()).isEqualTo("USE");
    }

    @Test
    @DisplayName("포인트 부족 시 예외를 던진다")
    void usePoint_whenInsufficientBalance() {
        // given
        Member member = memberRepository.save(Member.builder().name("회원3").point(1000).build());

        PointChargeServiceRequest request = PointChargeServiceRequest.builder()
                                                                     .memberId(member.getId())
                                                                     .amount(2000)
                                                                     .reason("포인트 부족 케이스")
                                                                     .build();

        // expect
        assertThatThrownBy(() -> pointService.usePoint(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("포인트 사용 실패");

        Member updated = memberRepository.findById(member.getId()).orElseThrow();
        assertThat(updated.getPoint()).isEqualTo(1000);
    }

}
