package com.higame.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_devices")
public class UserDevice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "device_id", nullable = false)
    private String deviceId;

    @Column(name = "device_name")
    private String deviceName;

    @Column(name = "device_model")
    private String deviceModel;

    @Column(name = "os_type")
    private String osType;

    @Column(name = "os_version")
    private String osVersion;

    @Column(name = "app_version")
    private String appVersion;

    @Column(name = "push_token")
    private String pushToken;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "last_active_time")
    private LocalDateTime lastActiveTime;

    @Column(name = "last_login_ip")
    private String lastLoginIp;

    @Column(name = "online")
    private boolean online = false;

    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}