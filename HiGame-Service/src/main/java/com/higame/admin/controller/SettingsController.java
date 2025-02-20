package com.higame.admin.controller;

import com.higame.admin.dto.SystemSettingsDTO;
import com.higame.admin.service.SettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/settings")
@Tag(name = "管理员-系统设置", description = "系统设置相关接口")
@RequiredArgsConstructor
public class SettingsController {

    private final SettingsService settingsService;

    @GetMapping
    @Operation(summary = "获取系统设置")
    public ResponseEntity<?> getSettings() {
        return settingsService.getSettings();
    }

    @PutMapping
    @Operation(summary = "更新系统设置")
    public ResponseEntity<?> updateSettings(@RequestBody SystemSettingsDTO settings) {
        return settingsService.updateSettings(settings);
    }
} 