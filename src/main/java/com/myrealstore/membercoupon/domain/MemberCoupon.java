package com.myrealstore.membercoupon.domain;

import java.time.LocalDateTime;

import com.myrealstore.coupon.domain.Coupon;
import com.myrealstore.global.common.BaseEntity;
import com.myrealstore.member.domain.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MemberCoupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Coupon coupon;

    @Column(name = "used", nullable = false)
    private boolean used = false;

    private LocalDateTime usedAt;

    public String getCouponName() {
        return this.coupon.getName();
    }

    public int useFor(int originalAmount) {
        markAsUsed();
        return this.coupon.calculateDiscount(originalAmount);
    }

    public int applyFor(int originalAmount) {
        return this.coupon.calculateDiscount(originalAmount);
    }

    public void verifyUsable(Long memberId) {
        coupon.verifyUsable();
        verifyOwnedBy(memberId);

        if (this.used) {
            throw new IllegalStateException("이미 사용된 쿠폰입니다.");
        }
    }

    private void verifyOwnedBy(Long memberId) {
        if (!this.member.getId().equals(memberId)) {
            throw new IllegalStateException("내 쿠폰만 사용할 수 있습니다.");
        }
    }

    private void markAsUsed() {
        this.used = true;
        this.usedAt = LocalDateTime.now();
    }
}
