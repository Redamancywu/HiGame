package com.higame.admin.controller;

import com.higame.service.UserStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@Tag(name = "管理员-数据面板", description = "数据统计相关接口")
@RequiredArgsConstructor
public class DashboardController {

    private final UserStatisticsService userStatisticsService;

    @GetMapping("/stats")
    @Operation(summary = "获取数据面板统计信息")
    public ResponseEntity<?> getStats() {
        return ResponseEntity.ok(userStatisticsService.getDailyStatistics());
    }

    @GetMapping("/user-trends")
    @Operation(summary = "获取用户趋势数据")
    public ResponseEntity<?> getUserTrends(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(7);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        return ResponseEntity.ok(userStatisticsService.getTrendStatistics(startDate, endDate));
    }
} 