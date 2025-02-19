package com.higame.admin.controller;

import com.higame.admin.dto.AdminLoginRequest;
import com.higame.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping({"/v1/admin", "/api/v1/admin"})
@Tag(name = "管理员", description = "管理员登录和权限管理相关接口")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "管理员登录", description = "使用管理员账号和密码进行登录")
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> adminLogin(
        @Parameter(description = "管理员登录信息", required = true)
        @RequestBody AdminLoginRequest request
    ) {
        logger.info("收到管理员登录请求，用户名: {}", request.getUsername());
        logger.debug("请求详情: {}", request.toString());
        
        try {
            // 验证管理员账号密码
            if ("admin".equals(request.getUsername()) && "sx991026@.".equals(request.getPassword())) {
                logger.info("管理员登录成功，用户名: {}", request.getUsername());
                
                // 生成管理员 token
                String token = jwtTokenProvider.generateToken(request.getUsername(), "ADMIN");
                logger.debug("已生成管理员用户token");
                
                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("username", request.getUsername());
                response.put("role", "ADMIN");
                
                logger.info("正在发送登录成功响应");
                return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
            }
            
            logger.warn("管理员登录失败 - 用户名或密码错误，用户名: {}", request.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("error", "用户名或密码错误"));
                
        } catch (Exception e) {
            logger.error("管理员登录过程中发生错误", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("error", "服务器内部错误: " + e.getMessage()));
        }
    }
}
