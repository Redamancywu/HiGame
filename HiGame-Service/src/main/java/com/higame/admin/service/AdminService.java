package com.higame.admin.service;

import com.higame.admin.dto.AdminLoginRequest;
import com.higame.admin.dto.AdminUpdateRequest;
import org.springframework.http.ResponseEntity;

public interface AdminService {
    ResponseEntity<?> login(AdminLoginRequest request);
    
    void logout(Long adminId);
    
    ResponseEntity<?> getAdminInfo(Long adminId);
    
    ResponseEntity<?> updateAdminInfo(Long adminId, AdminUpdateRequest request);
    
    ResponseEntity<?> changePassword(Long adminId, String oldPassword, String newPassword);
} 