package com.higame.dto;

import com.higame.entity.UserType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserActivityLogDTO {
    private Long id;
    private Long userId;
    private String username;
    private UserType userType;
    private String action;
    private String description;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime createTime;
}
