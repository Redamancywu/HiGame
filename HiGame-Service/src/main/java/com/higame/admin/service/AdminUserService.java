package com.higame.admin.service;

import com.higame.admin.dto.UserCreateRequest;
import com.higame.admin.dto.UserUpdateRequest;
import com.higame.admin.dto.UserStatusRequest;

import org.springframework.http.ResponseEntity;

public interface AdminUserService {
    ResponseEntity<?> getUserList(int page, int size, String query);
    ResponseEntity<?> createUser(UserCreateRequest request);
    ResponseEntity<?> updateUser(Long id, UserUpdateRequest request);
    ResponseEntity<?> deleteUser(Long id);
    ResponseEntity<?> updateUserStatus(Long id, UserStatusRequest request);
} 