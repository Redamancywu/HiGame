package com.higame.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "登录凭证不能为空")
    private String credential;  // 用户名/邮箱/手机号

    @NotBlank(message = "密码不能为空")
    private String password;

    private String verificationCode;  // 双重认证验证码

    private String deviceId;
    private String deviceName;
    private String deviceModel;
    private String osType;
    private String osVersion;
    private String appVersion;

    private boolean rememberMe = false;  // 是否记住登录状态
}