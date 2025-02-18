package com.higame.dto;

import com.higame.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {
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
}