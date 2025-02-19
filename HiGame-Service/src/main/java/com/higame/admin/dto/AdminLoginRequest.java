package com.higame.admin.dto;

import lombok.Data;

@Data
public class AdminLoginRequest {
    private String username;
    private String password;
}
