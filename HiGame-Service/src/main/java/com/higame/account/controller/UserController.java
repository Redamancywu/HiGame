package com.higame.account.controller;

import com.higame.account.dto.RegisterRequest;
import com.higame.account.service.UserService;
import com.higame.dto.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "用户管理", description = "用户注册、登录、信息管理等接口")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "用户注册", description = "使用用户名、密码和手机号进行完整注册")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "注册成功"),
        @ApiResponse(responseCode = "400", description = "参数错误"),
        @ApiResponse(responseCode = "409", description = "用户名已存在")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(
        @Parameter(description = "用户注册信息", required = true)
        @Valid @RequestBody RegisterRequest request,
        HttpServletRequest httpRequest
    ) {
        try {
            // 设置客户端IP
            request.setIp(getClientIp(httpRequest));
            request.setRequest(httpRequest);
            
            UserDTO userDTO = userService.register(request);
            return ResponseEntity.ok(userDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "简单注册", description = "仅使用设备ID进行简单注册")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "注册成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "409", description = "设备ID已被注册")
    })
    @PostMapping("/register/simple")
    public ResponseEntity<?> simpleRegister(
        @Parameter(description = "用户名", required = true) @RequestParam String username,
        @Parameter(description = "密码", required = true) @RequestParam String password
    ) {
        try {
            UserDTO userDTO = userService.registerSimple(username, password);
            return ResponseEntity.ok(userDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    @Operation(summary = "用户登录", description = "使用用户名和密码进行登录")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "登录成功"),
        @ApiResponse(responseCode = "400", description = "参数错误"),
        @ApiResponse(responseCode = "401", description = "用户名或密码错误")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(
        @Parameter(description = "用户名", required = true) @RequestParam String username,
        @Parameter(description = "密码", required = true) @RequestParam String password
    ) {
        try {
            return userService.login(username, password);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "获取用户信息", description = "获取当前登录用户的详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未登录或token已过期")
    })
    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(
        @Parameter(description = "用户ID", required = true)
        @RequestParam Long userId
    ) {
        try {
            return userService.getUserInfo(userId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "绑定第三方账号", description = "将第三方平台账号（如微信、QQ等）与当前账号绑定")
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
        @RequestParam String authCode
    ) {
        try {
            return userService.bindThirdParty(platform, authCode);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}