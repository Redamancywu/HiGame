package com.higame.entity;

import jakarta.persistence.*;
import lombok.Data;


import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_statistics")
public class UserStatistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @Column(name = "total_users", nullable = false)
    private Integer totalUsers = 0;

    @Column(name = "active_users", nullable = false)
    private Integer activeUsers = 0;

    @Column(name = "new_users", nullable = false)
    private Integer newUsers = 0;

    @Column(name = "date", nullable = false)
    private LocalDate date;

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
