package com.higame.controller;

import com.higame.dto.UserActivityLogDTO;
import com.higame.dto.UserStatisticsDTO;
import com.higame.entity.UserType;
import com.higame.service.UserActivityLogService;
import com.higame.service.UserStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    private final UserStatisticsService userStatisticsService;
    private final UserActivityLogService userActivityLogService;

    @GetMapping("/users")
    public ResponseEntity<UserStatisticsDTO> getUserStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) UserType userType) {
        return ResponseEntity.ok(userStatisticsService.getStatisticsByDate(date, userType));
    }

    @GetMapping("/users/trend")
    public ResponseEntity<List<UserStatisticsDTO>> getUserStatisticsTrend(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) UserType userType) {
        return ResponseEntity.ok(userStatisticsService.getStatisticsByDateRange(startDate, endDate, userType));
    }

    @GetMapping("/activities")
    public ResponseEntity<Page<UserActivityLogDTO>> getActivityLogs(
            @RequestParam(required = false) UserType userType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            Pageable pageable) {
        return ResponseEntity.ok(userActivityLogService.getActivityLogs(userType, startTime, endTime, pageable));
    }

    @GetMapping("/activities/recent")
    public ResponseEntity<List<UserActivityLogDTO>> getRecentActivities(
            @RequestParam(required = false) UserType userType,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(userActivityLogService.getRecentActivities(userType, limit));
    }

    @PostMapping("/users/update")
    public ResponseEntity<Void> updateUserStatistics(@RequestParam(required = false) UserType userType) {
        userStatisticsService.updateStatistics(userType);
        return ResponseEntity.ok().build();
    }
}
