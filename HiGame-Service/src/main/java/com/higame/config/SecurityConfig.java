package com.higame.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> {})  // 使用共享的 CORS 配置
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // Swagger UI
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/swagger-resources/**",
                    "/webjars/**"
                ).permitAll()
                // Public APIs
                .requestMatchers(
                    "/api/v1/user/register",
                    "/api/v1/user/register/simple",
                    "/api/v1/user/login",
                    "/v1/admin/login",     // 添加不带 /api 前缀的路径
                    "/api/v1/admin/login"  // 保留带 /api 前缀的路径
                ).permitAll()
                // Admin APIs
                .requestMatchers(
                    new AntPathRequestMatcher("/api/v1/admin/**"),
                    new AntPathRequestMatcher("/v1/admin/**")  // 添加不带 /api 前缀的路径
                ).hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .httpBasic(basic -> basic.disable());
        
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}