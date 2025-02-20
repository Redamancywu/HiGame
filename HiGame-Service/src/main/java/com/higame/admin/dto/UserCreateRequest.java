package com.higame.admin.dto;

import lombok.Data;

@Data
public class UserCreateRequest {
    private String username;
    private String password;
    private String email;
    private String phone;
    private String userType;
} 