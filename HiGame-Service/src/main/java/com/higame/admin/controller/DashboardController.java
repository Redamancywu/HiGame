package com.higame.admin.controller;

import com.higame.admin.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@Tag(name = "管理员-数据面板", description = "数据统计相关接口")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    @Operation(summary = "获取数据面板统计信息")
    public ResponseEntity<?> getStats() {
        return dashboardService.getStats();
    }

    @GetMapping("/user-trends")
    @Operation(summary = "获取用户趋势数据")
    public ResponseEntity<?> getUserTrends() {
        return dashboardService.getUserTrends();
    }
} 