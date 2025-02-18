package com.higame.account.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户注册请求DTO")
public class UserRegisterRequest {
    @Schema(description = "用户名", required = true)
    private String username;

    @Schema(description = "密码", required = true)
    private String password;
}