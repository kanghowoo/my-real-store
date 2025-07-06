package com.myrealstore.point.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.myrealstore.global.config.JpaAuditingConfig;
import com.myrealstore.global.config.QuerydslConfig;
import com.myrealstore.member.domain.Member;
import com.myrealstore.member.repository.MemberRepository;
import com.myrealstore.point.domain.Point;
import com.myrealstore.point.domain.PointEventType;

@DataJpaTest
@Import({ QuerydslConfig.class, JpaAuditingConfig.class })
@AutoConfigureTestDatabase(replace = Replace.NONE)
class PointRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PointRepository pointRepository;

    @Test
    @DisplayName("포인트 충전 이벤트가 발생하고 저장된다.")
    void saveAndFindPointCharge() {
        // given
        Member member = Member.builder()
                              .name("홍길동")
                              .point(0)
                              .build();
        Member savedMember = memberRepository.save(member);

        int amount = 1_000;
        Point pointCharge = Point.createCharge(savedMember, amount, "TEST");

        // when
        Point saved = pointRepository.save(pointCharge);

        // then
        assertThat(saved).isNotNull();
        assertThat(saved.getAmount()).isEqualTo(amount);
        assertThat(saved.getType()).isEqualTo(PointEventType.CHARGE);
        assertThat(saved.getMember().getId()).isEqualTo(savedMember.getId());
    }

    @Test
    @DisplayName("포인트 사용 이벤트가 발생하고 저장된다.")
    void saveAndFindPointUseWithMember() {
        // given
        Member member = Member.builder()
                              .name("홍길동")
                              .point(0)
                              .build();
        memberRepository.save(member);

        int amount = 1_000;
        Point pointCharge = Point.createUse(member, amount, "TEST");

        // when
        Point saved = pointRepository.save(pointCharge);

        // then
        assertThat(saved).isNotNull();
        assertThat(saved.getAmount()).isEqualTo(amount);
        assertThat(saved.getType()).isEqualTo(PointEventType.USE);
        assertThat(saved.getMember().getId()).isEqualTo(member.getId());
    }

}
