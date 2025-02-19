-- 修改用户表中的状态字段长度
ALTER TABLE `users` MODIFY COLUMN `status` VARCHAR(50) NOT NULL DEFAULT 'ACTIVE';

-- 修改设备表中的在线状态字段名
ALTER TABLE `user_devices` CHANGE COLUMN `is_online` `online` BOOLEAN DEFAULT FALSE;

-- 修改用户活动日志表的外键约束
ALTER TABLE `user_activity_logs` DROP FOREIGN KEY `user_activity_logs_ibfk_1`;
ALTER TABLE `user_activity_logs` ADD CONSTRAINT `user_activity_logs_ibfk_1` 
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;
