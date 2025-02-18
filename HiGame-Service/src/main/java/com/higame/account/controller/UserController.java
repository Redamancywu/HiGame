package com.higame.account.controller;

import com.higame.account.service.UserService;
import com.higame.dto.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "用户账号", description = "用户账号相关的API接口，包括注册、登录、第三方账号绑定等功能")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "用户注册", description = "通过手机号和密码注册新用户账号")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "注册成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "409", description = "手机号已被注册")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Parameter(description = "手机号", required = true) @RequestParam String phone,
            @Parameter(description = "密码", required = true) @RequestParam String password,
            @Parameter(description = "验证码", required = true) @RequestParam String code) {
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "用户登录", description = "使用手机号和密码登录账号")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "登录成功，返回JWT token"),
        @ApiResponse(responseCode = "401", description = "用户名或密码错误")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Parameter(description = "手机号", required = true) @RequestParam String phone,
            @Parameter(description = "密码", required = true) @RequestParam String password) {
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "简单注册", description = "仅需用户名和密码的简化版注册接口，主要用于测试目的")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "注册成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "409", description = "用户名已被注册")
    })
    @PostMapping("/register/simple")
    public ResponseEntity<?> simpleRegister(
            @Parameter(description = "用户名", required = true) @RequestParam String username,
            @Parameter(description = "密码", required = true) @RequestParam String password) {
        try {
            UserDTO userDTO = userService.registerSimple(username, password);
            return ResponseEntity.ok(userDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    @Operation(summary = "获取用户信息", description = "获取当前登录用户的详细信息")
    @SecurityRequirement(name = "bearer-jwt")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取用户信息"),
        @ApiResponse(responseCode = "401", description = "未登录或token已过期")
    })
    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo() {
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "绑定第三方账号", description = "将第三方平台账号（如微信、QQ等）与当前账号绑定")
    @SecurityRequirement(name = "bearer-jwt")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "绑定成功"),
        @ApiResponse(responseCode = "400", description = "绑定失败"),
        @ApiResponse(responseCode = "401", description = "未登录或token已过期")
    })
    @PostMapping("/bind/{platform}")
    public ResponseEntity<?> bindThirdParty(
            @Parameter(description = "第三方平台类型（如：wechat, qq）", required = true) 
            @PathVariable String platform,
            @Parameter(description = "第三方平台的授权码", required = true) 
            @RequestParam String authCode) {
        return ResponseEntity.ok().build();
    }
}