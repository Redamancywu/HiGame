package com.higame.admin.dto;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String email;
    private String phone;
    private String status;
} 