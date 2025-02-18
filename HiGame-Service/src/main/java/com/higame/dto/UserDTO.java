package com.higame.dto;

import com.higame.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {
    public enum RegisterType {
        SIMPLE,      // 简单注册
        EMAIL,       // 邮箱注册
        PHONE,       // 手机号注册
        THIRD_PARTY  // 第三方账号注册
    }

    private Long id;
    private String username;
    private String email;
    private String phone;
    private String nickname;
    private String avatar;
    private String gender;
    private LocalDateTime birthday;
    private User.UserStatus status;
    private boolean emailVerified;
    private boolean phoneVerified;
    private boolean twoFactorEnabled;
    private LocalDateTime lastLoginTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String verificationCode;
    private RegisterType registerType;
}