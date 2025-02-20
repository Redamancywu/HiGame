package com.higame.admin.controller;

import com.higame.admin.dto.UserCreateRequest;
import com.higame.admin.dto.UserUpdateRequest;
import com.higame.admin.dto.UserStatusRequest;
import com.higame.admin.service.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
@Tag(name = "管理员-用户管理", description = "用户管理相关接口")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    @Operation(summary = "获取用户列表")
    public ResponseEntity<?> getUserList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String query) {
        return adminUserService.getUserList(page, size, query);
    }

    @PostMapping
    @Operation(summary = "创建用户")
    public ResponseEntity<?> createUser(@RequestBody UserCreateRequest request) {
        return adminUserService.createUser(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新用户")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody UserUpdateRequest request) {
        return adminUserService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return adminUserService.deleteUser(id);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新用户状态")
    public ResponseEntity<?> updateUserStatus(
            @PathVariable Long id,
            @RequestBody UserStatusRequest request) {
        return adminUserService.updateUserStatus(id, request);
    }
} 