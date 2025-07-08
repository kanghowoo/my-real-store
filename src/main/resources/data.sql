-- 초기화
DELETE
FROM member_coupon;
DELETE
FROM point;
DELETE
FROM member;
DELETE
FROM coupon;
DELETE
FROM profile;

-- 프로필 10개 생성 (id 1~10)
INSERT INTO profile (id, name, view_count, created_at, updated_at)
VALUES (1, '홍길동1', 0, NOW(), NOW()),
       (2, '홍길동2', 0, NOW(), NOW()),
       (3, '홍길동3', 0, NOW(), NOW()),
       (4, '홍길동4', 0, NOW(), NOW()),
       (5, '홍길동5', 0, NOW(), NOW()),
       (6, '홍길동6', 0, NOW(), NOW()),
       (7, '홍길동7', 0, NOW(), NOW()),
       (8, '홍길동8', 0, NOW(), NOW()),
       (9, '홍길동9', 0, NOW(), NOW()),
       (10, '홍길동10', 0, NOW(), NOW());

-- 회원 10명 생성 (profile_id FK 참조)
INSERT INTO member (id, name, point, profile_id, created_at, updated_at)
VALUES (1, '회원1', 1000, 1, NOW(), NOW()),
       (2, '회원2', 1000, 2, NOW(), NOW()),
       (3, '회원3', 1000, 3, NOW(), NOW()),
       (4, '회원4', 1000, 4, NOW(), NOW()),
       (5, '회원5', 1000, 5, NOW(), NOW()),
       (6, '회원6', 1000, 6, NOW(), NOW()),
       (7, '회원7', 1000, 7, NOW(), NOW()),
       (8, '회원8', 1000, 8, NOW(), NOW()),
       (9, '회원9', 1000, 9, NOW(), NOW()),
       (10, '회원10', 1000, 10, NOW(), NOW());

-- 쿠폰 2개 생성
INSERT INTO coupon (id, name, discount_value, max_amount, discount_type, enabled, created_at, updated_at,
                    expired_at)
VALUES (1, '정액할인쿠폰', 1000, 1000, 'FIXED', b'1', NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY)),
       (2, '정률할인쿠폰', 20, 5000, 'PERCENT', b'1', NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY));

-- 회원 쿠폰 매핑 (used=0, 아직 사용 안 함)
INSERT INTO member_coupon (id, member_id, coupon_id, used, used_at, created_at, updated_at)
VALUES (1, 1, 1, b'0', NULL, NOW(), NOW()),
       (2, 2, 2, b'0', NULL, NOW(), NOW()),
       (3, 3, 1, b'0', NULL, NOW(), NOW()),
       (4, 4, 2, b'0', NULL, NOW(), NOW()),
       (5, 5, 1, b'0', NULL, NOW(), NOW()),
       (6, 6, 2, b'0', NULL, NOW(), NOW()),
       (7, 7, 1, b'0', NULL, NOW(), NOW()),
       (8, 8, 2, b'0', NULL, NOW(), NOW()),
       (9, 9, 1, b'0', NULL, NOW(), NOW()),
       (10, 10, 2, b'0', NULL, NOW(), NOW());
