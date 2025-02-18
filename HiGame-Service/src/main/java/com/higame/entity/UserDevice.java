package com.higame.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    private String deviceId;

    private String deviceName;

    private String deviceModel;

    private String osType;

    private String osVersion;

    private String appVersion;

    private String pushToken;

    private String refreshToken;

    private LocalDateTime lastActiveTime;

    private String lastLoginIp;

    private boolean isOnline;

    public void setIsOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    @CreationTimestamp
    private LocalDateTime createTime;

    @UpdateTimestamp
    private LocalDateTime updateTime;
}