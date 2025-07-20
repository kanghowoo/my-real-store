DROP TABLE IF EXISTS `member_coupon`;
DROP TABLE IF EXISTS `point`;
DROP TABLE IF EXISTS `member`;
DROP TABLE IF EXISTS `coupon`;
DROP TABLE IF EXISTS `profile`;
DROP TABLE IF EXISTS `payment`;

-- profile 테이블
CREATE TABLE `profile`
(
    `id`         BIGINT       NOT NULL AUTO_INCREMENT,
    `name`       VARCHAR(255) NOT NULL,
    `view_count` INT          NOT NULL,
    `created_at` DATETIME(6)  NOT NULL,
    `updated_at` DATETIME(6)  NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_profile_name` (`name`),
    KEY `idx_profile_view_count` (`view_count`),
    KEY `idx_profile_created_at` (`created_at` DESC)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- coupon 테이블
CREATE TABLE `coupon`
(
    `id`             BIGINT                   NOT NULL AUTO_INCREMENT,
    `name`           VARCHAR(255)             NOT NULL,
    `discount_type`  ENUM ('FIXED','PERCENT') NOT NULL,
    `discount_value` INT                      NOT NULL,
    `max_amount`     INT                      NOT NULL,
    `expired_at`     DATETIME(6)              NOT NULL,
    `enabled`        TINYINT(1)               NOT NULL,
    `created_at`     DATETIME(6)              NOT NULL,
    `updated_at`     DATETIME(6)              NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- member 테이블
CREATE TABLE `member`
(
    `id`         BIGINT       NOT NULL AUTO_INCREMENT,
    `name`       VARCHAR(255) NOT NULL,
    `profile_id` BIGINT DEFAULT NULL,
    `point`      INT          NOT NULL,
    `version`    bigint DEFAULT NULL,
    `created_at` DATETIME(6)  NOT NULL,
    `updated_at` DATETIME(6)  NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_member_profile_id` (`profile_id`),
    CONSTRAINT `fk_member_profile` FOREIGN KEY (`profile_id`) REFERENCES `profile` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- member_coupon 테이블
CREATE TABLE `member_coupon`
(
    `id`         BIGINT      NOT NULL AUTO_INCREMENT,
    `member_id`  BIGINT      NOT NULL,
    `coupon_id`  BIGINT      NOT NULL,
    `used`       TINYINT(1)  NOT NULL,
    `used_at`    DATETIME(6) DEFAULT NULL,
    `created_at` DATETIME(6) NOT NULL,
    `updated_at` DATETIME(6) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_member_coupon_coupon_id` (`coupon_id`),
    KEY `idx_member_coupon_member_id` (`member_id`),
    CONSTRAINT `fk_member_coupon_member` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`),
    CONSTRAINT `fk_member_coupon_coupon` FOREIGN KEY (`coupon_id`) REFERENCES `coupon` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE INDEX idx_member_coupon_available
    ON member_coupon (member_id, used, updated_at, created_at);

-- point 테이블
CREATE TABLE `point`
(
    `id`         BIGINT                NOT NULL AUTO_INCREMENT,
    `amount`     INT                   NOT NULL,
    `type`       ENUM ('CHARGE','USE') NOT NULL,
    `reason`     VARCHAR(255) DEFAULT NULL,
    `member_id`  BIGINT                NOT NULL,
    `created_at` DATETIME(6)           NOT NULL,
    `updated_at` DATETIME(6)           NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_point_member_id` (`member_id`),
    CONSTRAINT `fk_point_member` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


-- payment 테이블
CREATE TABLE `payment`
(
    `id`              bigint      NOT NULL AUTO_INCREMENT,
    `created_at`      datetime(6) NOT NULL,
    `member_id`       bigint                                    DEFAULT NULL,
    `updated_at`      datetime(6) NOT NULL,
    `order_id`        varchar(255)                              DEFAULT NULL,
    `payment_key`     varchar(255)                              DEFAULT NULL,
    `status`          enum ('CANCELED','DONE','FAILED','READY') DEFAULT NULL,
    `discount_amount` int                                       DEFAULT NULL,
    `final_amount`    int                                       DEFAULT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `uk_payment_order_id` UNIQUE (`order_id`),
    CONSTRAINT `uk_payment_key` UNIQUE (`payment_key`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
