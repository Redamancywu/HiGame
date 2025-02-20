package com.higame.admin.dto;

import lombok.Data;

@Data
public class AdminUpdateRequest {
    private String email;
    private String phone;
    // 其他可更新的字段
} 