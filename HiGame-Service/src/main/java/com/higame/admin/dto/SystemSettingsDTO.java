package com.higame.admin.dto;

import lombok.Data;

@Data
public class SystemSettingsDTO {
    private String siteName;
    private String siteDescription;
    private String adminEmail;
    private Boolean enableRegistration;
    private Boolean enableEmailVerification;
    private Integer sessionTimeout;
    private String theme;
} 