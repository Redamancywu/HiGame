package com.higame.admin.service.impl;

import com.higame.admin.dto.AdminLoginRequest;
import com.higame.admin.dto.AdminUpdateRequest;
import com.higame.admin.service.AdminService;
import com.higame.entity.Admin;
import com.higame.repository.AdminRepository;
import com.higame.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public ResponseEntity<?> login(AdminLoginRequest request) {
        Admin admin = adminRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));
            
        if (!admin.isEnabled()) {
            throw new RuntimeException("账号已被禁用");
        }
        
        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        String token = jwtTokenProvider.generateToken(admin.getUsername(), admin.getRole());
        
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("username", admin.getUsername());
        response.put("role", admin.getRole());
        
        return ResponseEntity.ok(response);
    }

    @Override
    @Transactional
    public void logout(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("管理员不存在"));
        
        // 可以在这里添加登出时的清理工作
        // 比如清除token缓存、更新最后登出时间等
        admin.setLastLogoutTime(LocalDateTime.now());
        adminRepository.save(admin);
    }

    @Override
    public ResponseEntity<?> getAdminInfo(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("管理员不存在"));
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", admin.getId());
        response.put("username", admin.getUsername());
        response.put("email", admin.getEmail());
        response.put("phone", admin.getPhone());
        response.put("role", "ADMIN");
        response.put("createTime", admin.getCreateTime());
        response.put("lastLoginTime", admin.getLastLoginTime());
        
        return ResponseEntity.ok(response);
    }

    @Override
    @Transactional
    public ResponseEntity<?> updateAdminInfo(Long adminId, AdminUpdateRequest request) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("管理员不存在"));

        if (request.getEmail() != null) {
            admin.setEmail(request.getEmail());
        }
        
        if (request.getPhone() != null) {
            admin.setPhone(request.getPhone());
        }

        admin = adminRepository.save(admin);
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", admin.getId());
        response.put("username", admin.getUsername());
        response.put("email", admin.getEmail());
        response.put("phone", admin.getPhone());
        response.put("updateTime", admin.getUpdateTime());
        
        return ResponseEntity.ok(response);
    }

    @Override
    @Transactional
    public ResponseEntity<?> changePassword(Long adminId, String oldPassword, String newPassword) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("管理员不存在"));

        if (!passwordEncoder.matches(oldPassword, admin.getPassword())) {
            throw new RuntimeException("原密码错误");
        }

        // 验证新密码格式
        if (newPassword == null || newPassword.length() < 6) {
            throw new RuntimeException("新密码长度不能小于6位");
        }

        admin.setPassword(passwordEncoder.encode(newPassword));
        admin.setUpdateTime(LocalDateTime.now());
        adminRepository.save(admin);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "密码修改成功");
        response.put("updateTime", admin.getUpdateTime());
        
        return ResponseEntity.ok(response);
    }
} 