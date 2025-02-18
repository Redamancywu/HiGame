-- 插入测试用户数据
INSERT INTO `user` (`username`, `password`, `email`, `phone`, `nickname`, `avatar_url`, `status`, `last_login_time`, `last_login_ip`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTTEn7Z/oqu', 'admin@higame.com', '13800000000', '管理员', 'https://example.com/avatars/admin.png', 1, NOW(), '127.0.0.1'),
('test', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTTEn7Z/oqu', 'test@higame.com', '13800000001', '测试用户', 'https://example.com/avatars/test.png', 1, NOW(), '127.0.0.1');

-- 插入测试用户设备数据
INSERT INTO `user_device` (`user_id`, `device_id`, `device_type`, `device_model`, `os_version`, `app_version`, `last_login_time`) VALUES
(1, 'device_id_001', 'android', 'Pixel 6', 'Android 12', '1.0.0', NOW()),
(2, 'device_id_002', 'ios', 'iPhone 13', 'iOS 15', '1.0.0', NOW());

-- 插入测试第三方账号绑定数据
INSERT INTO `user_third_party` (`user_id`, `platform`, `open_id`, `union_id`, `nickname`, `avatar_url`) VALUES
(1, 'wechat', 'wx_openid_001', 'wx_unionid_001', '微信昵称1', 'https://example.com/wx_avatar1.png'),
(2, 'qq', 'qq_openid_001', NULL, 'QQ昵称1', 'https://example.com/qq_avatar1.png');

-- 插入测试游戏数据
INSERT INTO `game` (`name`, `description`, `icon_url`, `package_name`) VALUES
('测试游戏1', '这是一个测试游戏', 'https://example.com/icon1.png', 'com.higame.test1'),
('测试游戏2', '这是另一个测试游戏', 'https://example.com/icon2.png', 'com.higame.test2');

-- 插入测试支付订单数据
INSERT INTO `payment_order` (`order_no`, `user_id`, `game_id`, `amount`, `status`, `payment_method`) VALUES
('ORDER202401010001', 1, 1, 6.99, 1, 'alipay'),
('ORDER202401010002', 2, 2, 9.99, 1, 'wechat');

-- 插入测试广告记录数据
INSERT INTO `ad_record` (`user_id`, `game_id`, `ad_type`, `ad_platform`, `revenue`) VALUES
(1, 1, 'rewarded', 'admob', 0.05),
(2, 2, 'interstitial', 'unity', 0.03);