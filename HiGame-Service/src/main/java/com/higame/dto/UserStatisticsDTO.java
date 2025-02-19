package com.higame.dto;

import com.higame.entity.UserType;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserStatisticsDTO {
    private Long id;
    private UserType userType;
    private Integer totalUsers;
    private Integer activeUsers;
    private Integer newUsers;
    private LocalDate date;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
