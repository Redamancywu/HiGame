package com.higame.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .authorizeHttpRequests(auth -> auth
                // 登录相关接口
                .requestMatchers(
                    "/api/v1/admin/login",
                    "/v1/admin/login"
                ).permitAll()
                // Swagger UI 接口
                .requestMatchers(
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-resources/**",
                    "/webjars/**"
                ).permitAll()
                // 数据面板接口
                .requestMatchers(
                    "/api/v1/admin/dashboard/**",
                    "/v1/admin/dashboard/**"
                ).permitAll()
                // 用户管理接口
                .requestMatchers(
                    "/api/v1/admin/users/**",
                    "/v1/admin/users/**"
                ).permitAll()
                // 设备管理接口
                .requestMatchers(
                    "/api/v1/admin/devices/**",
                    "/v1/admin/devices/**"
                ).permitAll()
                // 系统设置接口
                .requestMatchers(
                    "/api/v1/admin/settings/**",
                    "/v1/admin/settings/**"
                ).permitAll()
                // 错误页面
                .requestMatchers("/error").permitAll()
                // 其他请求需要认证
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );
            
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}