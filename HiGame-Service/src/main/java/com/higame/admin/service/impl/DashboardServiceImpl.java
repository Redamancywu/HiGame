package com.higame.admin.service.impl;

import com.higame.admin.dto.DashboardStatsDTO;
import com.higame.admin.service.DashboardService;
import com.higame.entity.UserType;
import com.higame.repository.UserDeviceRepository;
import com.higame.repository.UserRepository;
import com.higame.repository.UserStatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final UserDeviceRepository userDeviceRepository;
    private final UserStatisticsRepository userStatisticsRepository;

    @Override
    public ResponseEntity<?> getStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();
        
        // 获取用户总数
        stats.setTotalUsers(userRepository.count());
        
        // 获取SDK用户数
        stats.setSdkUsers(userRepository.countByUserType(UserType.SDK));
        
        // 获取APP用户数
        stats.setAppUsers(userRepository.countByUserType(UserType.APP));
        
        // 获取今日活跃用户数
        stats.setActiveUsers(userRepository.countActiveUsersByDate(LocalDate.now()));
        
        // 获取今日新增用户数
        stats.setNewUsers(userRepository.countNewUsersByDate(LocalDate.now()));
        
        // 获取在线设备数
        stats.setOnlineDevices(userDeviceRepository.countByOnlineTrue());

        return ResponseEntity.ok(stats);
    }

    @Override
    public ResponseEntity<?> getUserTrends() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(7);
        
        List<Map<String, Object>> trends = userStatisticsRepository
            .findUserTrendsByDateRange(startDate, endDate);
            
        Map<String, Object> response = new HashMap<>();
        response.put("trends", trends);
        response.put("startDate", startDate);
        response.put("endDate", endDate);
        
        return ResponseEntity.ok(response);
    }
} 