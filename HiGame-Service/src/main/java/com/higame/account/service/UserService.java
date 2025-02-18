package com.higame.account.service;

import com.higame.dto.LoginRequest;
import com.higame.dto.RegisterRequest;
import com.higame.dto.UserDTO;
import com.higame.entity.User;
import com.higame.entity.User.UserStatus;
import com.higame.entity.UserDevice;
import java.util.List;

public interface UserService {
    // 用户注册
    UserDTO register(RegisterRequest request);
    
    // 简单注册
    UserDTO registerSimple(String username, String password);
    
    // 用户登录
    UserDTO login(LoginRequest request, String ipAddress);
    
    // 用户登出
    void logout(Long userId, String deviceId);
    
    // 刷新令牌
    String refreshToken(String refreshToken);
    
    // 修改密码
    void changePassword(Long userId, String oldPassword, String newPassword);
    
    // 重置密码
    void resetPassword(String credential, String verificationCode, String newPassword);
    
    // 发送验证码
    void sendVerificationCode(String type, String target);
    
    // 验证验证码
    boolean verifyCode(String type, String target, String code);
    
    // 更新用户信息
    UserDTO updateUserInfo(Long userId, UserDTO userDTO);
    
    // 获取用户信息
    UserDTO getUserInfo(Long userId);
    
    // 获取用户设备列表
    List<UserDevice> getUserDevices(Long userId);
    
    // 强制下线其他设备
    void logoutOtherDevices(Long userId, String currentDeviceId);
    
    // 启用/禁用双重认证
    void toggleTwoFactorAuth(Long userId, boolean enable);
    
    // 更新用户状态
    void updateUserStatus(Long userId, UserStatus status, String reason, Integer banDays);
    
    // 绑定第三方账号
    void bindThirdPartyAccount(Long userId, String platform, String thirdPartyId);
    
    // 解绑第三方账号
    void unbindThirdPartyAccount(Long userId, String type);
    
    // 第三方登录
    UserDTO loginWithThirdParty(String type, String thirdPartyToken, String deviceId);
}