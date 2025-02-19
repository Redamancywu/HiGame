package com.higame.service.impl;

import com.higame.dto.UserStatisticsDTO;
import com.higame.entity.UserStatistics;
import com.higame.entity.UserType;
import com.higame.repository.UserRepository;
import com.higame.repository.UserStatisticsRepository;
import com.higame.service.UserStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserStatisticsServiceImpl implements UserStatisticsService {
    private final UserStatisticsRepository userStatisticsRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatisticsDTO getStatisticsByDate(LocalDate date, UserType userType) {
        return userStatisticsRepository.findByDateAndUserType(date, userType)
                .map(this::convertToDTO)
                .orElseGet(() -> {
                    UserStatistics statistics = new UserStatistics();
                    statistics.setDate(date);
                    statistics.setUserType(userType);
                    return convertToDTO(statistics);
                });
    }

    @Override
    public List<UserStatisticsDTO> getStatisticsByDateRange(LocalDate startDate, LocalDate endDate, UserType userType) {
        return userStatisticsRepository.findByDateBetweenAndUserType(startDate, endDate, userType)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateStatistics(UserType userType) {
        LocalDate today = LocalDate.now();
        Optional<UserStatistics> statisticsOpt = userStatisticsRepository.findByDateAndUserType(today, userType);
        
        UserStatistics statistics = statisticsOpt.orElseGet(() -> {
            UserStatistics newStats = new UserStatistics();
            newStats.setDate(today);
            newStats.setUserType(userType);
            return newStats;
        });

        // 更新总用户数
        long totalUsers = userRepository.countByUserType(userType);
        statistics.setTotalUsers((int) totalUsers);

        // 更新活跃用户数（当天登录的用户）
        long activeUsers = userRepository.countActiveUsersByType(userType, today);
        statistics.setActiveUsers((int) activeUsers);

        // 更新新增用户数
        long newUsers = userRepository.countNewUsersByType(userType, today);
        statistics.setNewUsers((int) newUsers);

        userStatisticsRepository.save(statistics);
    }

    @Override
    @Transactional
    public void incrementNewUsers(UserType userType) {
        LocalDate today = LocalDate.now();
        UserStatistics statistics = getUserStatistics(today, userType);
        statistics.setNewUsers(statistics.getNewUsers() + 1);
        statistics.setTotalUsers(statistics.getTotalUsers() + 1);
        userStatisticsRepository.save(statistics);
    }

    @Override
    @Transactional
    public void incrementActiveUsers(UserType userType) {
        LocalDate today = LocalDate.now();
        UserStatistics statistics = getUserStatistics(today, userType);
        statistics.setActiveUsers(statistics.getActiveUsers() + 1);
        userStatisticsRepository.save(statistics);
    }

    private UserStatistics getUserStatistics(LocalDate date, UserType userType) {
        return userStatisticsRepository.findByDateAndUserType(date, userType)
                .orElseGet(() -> {
                    UserStatistics newStats = new UserStatistics();
                    newStats.setDate(date);
                    newStats.setUserType(userType);
                    return newStats;
                });
    }

    private UserStatisticsDTO convertToDTO(UserStatistics statistics) {
        UserStatisticsDTO dto = new UserStatisticsDTO();
        dto.setId(statistics.getId());
        dto.setUserType(statistics.getUserType());
        dto.setTotalUsers(statistics.getTotalUsers());
        dto.setActiveUsers(statistics.getActiveUsers());
        dto.setNewUsers(statistics.getNewUsers());
        dto.setDate(statistics.getDate());
        dto.setCreateTime(statistics.getCreateTime());
        dto.setUpdateTime(statistics.getUpdateTime());
        return dto;
    }
}
