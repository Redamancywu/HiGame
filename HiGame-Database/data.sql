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

-- 插入超级管理员账号 (密码: sx991026..)
INSERT INTO admins (username, password, email, role, enabled, create_time)
VALUES (
    'admin-client',
    '$2a$10$6Nt0sQNUvdI7YAWLhkl9UOBzwWoFVpkQxFGrVXDxGQXgVVYHpWJSi',
    'admin@higame.com',
    'ADMIN',
    true,
    NOW()
) ON DUPLICATE KEY UPDATE
    password = VALUES(password),
    email = VALUES(email),
    role = VALUES(role),
    enabled = VALUES(enabled);

-- 插入20条测试用户数据
INSERT INTO users (username, password, email, phone, nickname, user_type, status, create_time)
VALUES 
('user001', '$2a$10$6Nt0sQNUvdI7YAWLhkl9UOBzwWoFVpkQxFGrVXDxGQXgVVYHpWJSi', 'user1@test.com', '13800000001', '游客001', 'APP', 'ACTIVE', DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY)),
('user002', '$2a$10$6Nt0sQNUvdI7YAWLhkl9UOBzwWoFVpkQxFGrVXDxGQXgVVYHpWJSi', 'user2@test.com', '13800000002', '游客002', 'SDK', 'ACTIVE', DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY)),
('user003', '$2a$10$6Nt0sQNUvdI7YAWLhkl9UOBzwWoFVpkQxFGrVXDxGQXgVVYHpWJSi', 'user3@test.com', '13800000003', '游客003', 'APP', 'BANNED', DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY)),
('user004', '$2a$10$6Nt0sQNUvdI7YAWLhkl9UOBzwWoFVpkQxFGrVXDxGQXgVVYHpWJSi', 'user4@test.com', '13800000004', '游客004', 'SDK', 'ACTIVE', DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY)),
('user005', '$2a$10$6Nt0sQNUvdI7YAWLhkl9UOBzwWoFVpkQxFGrVXDxGQXgVVYHpWJSi', 'user5@test.com', '13800000005', '游客005', 'APP', 'ACTIVE', DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY)),
('user006', '$2a$10$6Nt0sQNUvdI7YAWLhkl9UOBzwWoFVpkQxFGrVXDxGQXgVVYHpWJSi', 'user6@test.com', '13800000006', '游客006', 'SDK', 'ACTIVE', DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY)),
('user007', '$2a$10$6Nt0sQNUvdI7YAWLhkl9UOBzwWoFVpkQxFGrVXDxGQXgVVYHpWJSi', 'user7@test.com', '13800000007', '游客007', 'APP', 'ACTIVE', DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY)),
('user008', '$2a$10$6Nt0sQNUvdI7YAWLhkl9UOBzwWoFVpkQxFGrVXDxGQXgVVYHpWJSi', 'user8@test.com', '13800000008', '游客008', 'SDK', 'BANNED', DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY)),
('user009', '$2a$10$6Nt0sQNUvdI7YAWLhkl9UOBzwWoFVpkQxFGrVXDxGQXgVVYHpWJSi', 'user9@test.com', '13800000009', '游客009', 'APP', 'ACTIVE', DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY)),
('user010', '$2a$10$6Nt0sQNUvdI7YAWLhkl9UOBzwWoFVpkQxFGrVXDxGQXgVVYHpWJSi', 'user10@test.com', '13800000010', '游客010', 'SDK', 'ACTIVE', DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY)),
('user011', '$2a$10$6Nt0sQNUvdI7YAWLhkl9UOBzwWoFVpkQxFGrVXDxGQXgVVYHpWJSi', 'user11@test.com', '13800000011', '游客011', 'APP', 'ACTIVE', DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY)),
('user012', '$2a$10$6Nt0sQNUvdI7YAWLhkl9UOBzwWoFVpkQxFGrVXDxGQXgVVYHpWJSi', 'user12@test.com', '13800000012', '游客012', 'SDK', 'ACTIVE', DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY)),
('user013', '$2a$10$6Nt0sQNUvdI7YAWLhkl9UOBzwWoFVpkQxFGrVXDxGQXgVVYHpWJSi', 'user13@test.com', '13800000013', '游客013', 'APP', 'BANNED', DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY)),
('user014', '$2a$10$6Nt0sQNUvdI7YAWLhkl9UOBzwWoFVpkQxFGrVXDxGQXgVVYHpWJSi', 'user14@test.com', '13800000014', '游客014', 'SDK', 'ACTIVE', DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY)),
('user015', '$2a$10$6Nt0sQNUvdI7YAWLhkl9UOBzwWoFVpkQxFGrVXDxGQXgVVYHpWJSi', 'user15@test.com', '13800000015', '游客015', 'APP', 'ACTIVE', DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY)),
('user016', '$2a$10$6Nt0sQNUvdI7YAWLhkl9UOBzwWoFVpkQxFGrVXDxGQXgVVYHpWJSi', 'user16@test.com', '13800000016', '游客016', 'SDK', 'ACTIVE', DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY)),
('user017', '$2a$10$6Nt0sQNUvdI7YAWLhkl9UOBzwWoFVpkQxFGrVXDxGQXgVVYHpWJSi', 'user17@test.com', '13800000017', '游客017', 'APP', 'ACTIVE', DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY)),
('user018', '$2a$10$6Nt0sQNUvdI7YAWLhkl9UOBzwWoFVpkQxFGrVXDxGQXgVVYHpWJSi', 'user18@test.com', '13800000018', '游客018', 'SDK', 'BANNED', DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY)),
('user019', '$2a$10$6Nt0sQNUvdI7YAWLhkl9UOBzwWoFVpkQxFGrVXDxGQXgVVYHpWJSi', 'user19@test.com', '13800000019', '游客019', 'APP', 'ACTIVE', DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY)),
('user020', '$2a$10$6Nt0sQNUvdI7YAWLhkl9UOBzwWoFVpkQxFGrVXDxGQXgVVYHpWJSi', 'user20@test.com', '13800000020', '游客020', 'SDK', 'ACTIVE', DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY));

-- 插入随机设备数据
INSERT INTO user_devices (user_id, device_id, device_name, device_model, os_type, os_version, app_version, online, last_active_time)
SELECT 
    id,
    CONCAT('device_', LPAD(id, 3, '0')),
    CASE FLOOR(RAND() * 4)
        WHEN 0 THEN 'iPhone 13'
        WHEN 1 THEN 'Samsung S21'
        WHEN 2 THEN 'Xiaomi 12'
        WHEN 3 THEN 'OPPO Find X3'
    END,
    CASE FLOOR(RAND() * 4)
        WHEN 0 THEN 'iPhone13,2'
        WHEN 1 THEN 'SM-G991B'
        WHEN 2 THEN 'M2012K11C'
        WHEN 3 THEN 'PFEM10'
    END,
    CASE FLOOR(RAND() * 2)
        WHEN 0 THEN 'iOS'
        WHEN 1 THEN 'Android'
    END,
    CASE FLOOR(RAND() * 2)
        WHEN 0 THEN '15.0'
        WHEN 1 THEN '12'
    END,
    '1.0.0',
    CASE FLOOR(RAND() * 2)
        WHEN 0 THEN true
        WHEN 1 THEN false
    END,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 7) DAY)
FROM users;

-- 插入用户统计数据（最近7天）
INSERT INTO user_statistics (user_type, total_users, active_users, new_users, date)
SELECT 
    user_type,
    100 + FLOOR(RAND() * 100) as total_users,
    50 + FLOOR(RAND() * 50) as active_users,
    5 + FLOOR(RAND() * 15) as new_users,
    DATE_SUB(CURDATE(), INTERVAL day DAY) as date
FROM 
    (SELECT 'APP' as user_type UNION SELECT 'SDK') types,
    (SELECT 0 as day UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6) days
ORDER BY date DESC;