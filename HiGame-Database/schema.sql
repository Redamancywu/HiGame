-- 创建数据库
CREATE DATABASE IF NOT EXISTS `higame_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE `higame_db`;

-- 创建管理员表
CREATE TABLE IF NOT EXISTS `admins` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `email` VARCHAR(100),
    `phone` VARCHAR(20),
    `role` VARCHAR(20) NOT NULL DEFAULT 'ADMIN',
    `enabled` BOOLEAN DEFAULT TRUE,
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `last_login_time` DATETIME,
    `last_logout_time` DATETIME,
    `last_login_ip` VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建用户表
CREATE TABLE IF NOT EXISTS `users` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `email` VARCHAR(100) UNIQUE,
    `phone` VARCHAR(20) UNIQUE,
    `nickname` VARCHAR(50),
    `avatar` VARCHAR(255),
    `register_type` VARCHAR(20) NOT NULL DEFAULT 'simple',
    `user_type` VARCHAR(10) NOT NULL DEFAULT 'APP',
    `gender` VARCHAR(10),
    `birthday` DATETIME,
    `status` VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    `ban_reason` VARCHAR(255),
    `ban_expire_time` DATETIME,
    `email_verified` BOOLEAN DEFAULT FALSE,
    `phone_verified` BOOLEAN DEFAULT FALSE,
    `two_factor_enabled` BOOLEAN DEFAULT FALSE,
    `two_factor_secret` VARCHAR(100),
    `security_question` VARCHAR(255),
    `security_answer` VARCHAR(255),
    `login_fail_count` INT DEFAULT 0,
    `last_login_time` DATETIME,
    `last_login_ip` VARCHAR(50),
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建用户设备表
CREATE TABLE IF NOT EXISTS `user_devices` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `device_id` VARCHAR(100) NOT NULL,
    `device_name` VARCHAR(100),
    `device_model` VARCHAR(50),
    `os_type` VARCHAR(20),
    `os_version` VARCHAR(20),
    `app_version` VARCHAR(20),
    `push_token` VARCHAR(255),
    `refresh_token` VARCHAR(255),
    `last_active_time` DATETIME,
    `last_login_ip` VARCHAR(50),
    `online` BOOLEAN DEFAULT FALSE,
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建用户统计表
CREATE TABLE IF NOT EXISTS `user_statistics` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_type` VARCHAR(10) NOT NULL COMMENT 'SDK/APP',
    `total_users` INT NOT NULL DEFAULT 0,
    `active_users` INT NOT NULL DEFAULT 0,
    `new_users` INT NOT NULL DEFAULT 0,
    `date` DATE NOT NULL,
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_type_date` (`user_type`, `date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建第三方账号表
CREATE TABLE IF NOT EXISTS `third_party_accounts` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `type` VARCHAR(20) NOT NULL,
    `platform` VARCHAR(20) NOT NULL,
    `third_party_user_id` VARCHAR(100) NOT NULL,
    `access_token` VARCHAR(255),
    `refresh_token` VARCHAR(255),
    `token_expire_time` DATETIME,
    `nickname` VARCHAR(50),
    `avatar` VARCHAR(255),
    `email` VARCHAR(100),
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE KEY `uk_platform_userid` (`platform`, `third_party_user_id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建游戏表
CREATE TABLE IF NOT EXISTS `game` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(100) NOT NULL,
    `description` TEXT,
    `icon_url` VARCHAR(255),
    `package_name` VARCHAR(100) NOT NULL UNIQUE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建支付订单表
CREATE TABLE IF NOT EXISTS `payment_order` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `order_no` VARCHAR(50) NOT NULL UNIQUE,
    `user_id` BIGINT NOT NULL,
    `game_id` BIGINT NOT NULL,
    `amount` DECIMAL(10,2) NOT NULL,
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '0:待支付 1:支付成功 2:支付失败',
    `payment_method` VARCHAR(20) NOT NULL COMMENT 'alipay/wechat/google/apple',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (game_id) REFERENCES game(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建广告记录表
CREATE TABLE IF NOT EXISTS `ad_record` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `game_id` BIGINT NOT NULL,
    `ad_type` VARCHAR(20) NOT NULL COMMENT 'banner/interstitial/rewarded',
    `ad_platform` VARCHAR(20) NOT NULL COMMENT 'admob/unity/facebook',
    `revenue` DECIMAL(10,4) NOT NULL DEFAULT 0,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (game_id) REFERENCES game(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;