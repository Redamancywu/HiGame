package com.higame.admin.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserStatusRequest {
    private String status;
    private String reason;
    private LocalDateTime expireTime;
} 