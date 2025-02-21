package com.higame.admin.controller;

import com.higame.admin.dto.UserCreateRequest;
import com.higame.admin.dto.UserUpdateRequest;
import com.higame.admin.dto.UserStatusRequest;
import com.higame.admin.service.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping({"/v1/admin/users", "/api/v1/admin/users"})
@Tag(name = "管理员-用户管理", description = "用户管理相关接口")
@RequiredArgsConstructor
@Slf4j
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    @Operation(summary = "获取用户列表")
    public ResponseEntity<?> getUserList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String query) {
        log.info("获取用户列表请求: page={}, size={}, query={}", page, size, query);
        try {
            return adminUserService.getUserList(page, size, query);
        } catch (Exception e) {
            log.error("获取用户列表失败", e);
            return ResponseEntity.badRequest().body(Map.of(
                "message", "获取用户列表失败",
                "error", e.getMessage()
            ));
        }
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