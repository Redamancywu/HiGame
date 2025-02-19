-- 添加用户类型字段
ALTER TABLE `users`
ADD COLUMN `user_type` VARCHAR(10) NOT NULL DEFAULT 'APP' COMMENT 'SDK/APP' AFTER `register_type`;

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

-- 创建用户活动日志表
CREATE TABLE IF NOT EXISTS `user_activity_logs` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `user_type` VARCHAR(10) NOT NULL COMMENT 'SDK/APP',
    `action` VARCHAR(50) NOT NULL,
    `description` TEXT,
    `ip_address` VARCHAR(50),
    `user_agent` VARCHAR(255),
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX `idx_user_type` (`user_type`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 删除不需要的游戏相关表
DROP TABLE IF EXISTS `ad_record`;
DROP TABLE IF EXISTS `payment_order`;
DROP TABLE IF EXISTS `game`;
