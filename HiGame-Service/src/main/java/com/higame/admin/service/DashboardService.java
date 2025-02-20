package com.higame.admin.service;

import org.springframework.http.ResponseEntity;

public interface DashboardService {
    ResponseEntity<?> getStats();
    ResponseEntity<?> getUserTrends();
} 