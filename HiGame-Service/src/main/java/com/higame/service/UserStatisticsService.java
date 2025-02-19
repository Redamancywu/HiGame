package com.higame.service;

import com.higame.dto.UserStatisticsDTO;
import com.higame.entity.UserType;

import java.time.LocalDate;
import java.util.List;

public interface UserStatisticsService {
    UserStatisticsDTO getStatisticsByDate(LocalDate date, UserType userType);
    List<UserStatisticsDTO> getStatisticsByDateRange(LocalDate startDate, LocalDate endDate, UserType userType);
    void updateStatistics(UserType userType);
    void incrementNewUsers(UserType userType);
    void incrementActiveUsers(UserType userType);
}
