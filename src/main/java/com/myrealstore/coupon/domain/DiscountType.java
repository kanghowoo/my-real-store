package com.myrealstore.coupon.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DiscountType {
    FIXED("정액할인") {
        @Override
        public int calculate(int originalAmount, int discountValue, int maxDiscountAmount) {
            return discountValue;
        }
    },
    PERCENT("정률할인") {
        @Override
        public int calculate(int originalAmount, int discountValue, int maxDiscountAmount) {
            int discounted = (originalAmount * discountValue) / 100;
            return Math.min(discounted, maxDiscountAmount);
        }
    };

    private final String description;

    public abstract int calculate(int originalAmount, int discountValue, int maxDiscountAmount);
}
