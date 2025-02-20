package com.higame.admin.service;

import com.higame.admin.dto.SystemSettingsDTO;
import org.springframework.http.ResponseEntity;

public interface SettingsService {
    ResponseEntity<?> getSettings();
    ResponseEntity<?> updateSettings(SystemSettingsDTO settings);
} 