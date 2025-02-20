package com.higame.admin.service.impl;

import com.higame.admin.dto.SystemSettingsDTO;
import com.higame.admin.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SettingsServiceImpl implements SettingsService {

    @Override
    public ResponseEntity<?> getSettings() {
        SystemSettingsDTO settings = new SystemSettingsDTO();
        // TODO: 从数据库或配置文件加载设置
        return ResponseEntity.ok(settings);
    }

    @Override
    public ResponseEntity<?> updateSettings(SystemSettingsDTO settings) {
        // TODO: 保存设置到数据库或配置文件
        return ResponseEntity.ok(settings);
    }
} 