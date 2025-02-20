package com.higame.admin.dto;

import lombok.Data;

@Data
public class DashboardStatsDTO {
    private long totalUsers;
    private long sdkUsers;
    private long appUsers;
    private long activeUsers;
    private long newUsers;
    private long onlineDevices;
} 