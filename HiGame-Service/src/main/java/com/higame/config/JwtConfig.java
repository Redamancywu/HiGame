package com.higame.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    private String secret;
    private long accessTokenExpiration = 3600; // 访问令牌过期时间（秒）
    private long refreshTokenExpiration = 604800; // 刷新令牌过期时间（秒）
    private String issuer = "HiGame";
    
    // 用于生成token的密钥
    private String base64Secret;
    
    // token类型
    private String tokenType = "Bearer";
    
    // token在header中的名称
    private String headerName = "Authorization";
    
    // 刷新token在header中的名称
    private String refreshHeaderName = "Refresh-Token";
}