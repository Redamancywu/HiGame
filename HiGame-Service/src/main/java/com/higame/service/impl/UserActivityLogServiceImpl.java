package com.higame.service.impl;

import com.higame.dto.UserActivityLogDTO;
import com.higame.entity.User;
import com.higame.entity.UserActivityLog;
import com.higame.entity.UserType;
import com.higame.repository.UserActivityLogRepository;
import com.higame.service.UserActivityLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserActivityLogServiceImpl implements UserActivityLogService {
    private final UserActivityLogRepository userActivityLogRepository;

    @Transactional
    @Override
    public void logActivity(User user, String action, String description, HttpServletRequest request) {
        UserActivityLog log = new UserActivityLog();
        log.setUser(user);
        log.setUserType(user.getUserType());
        log.setAction(action);
        log.setDescription(description);
        log.setIpAddress(getClientIp(request));
        log.setUserAgent(request.getHeader("User-Agent"));
        userActivityLogRepository.save(log);
    }

    @Override
    public Page<UserActivityLogDTO> getActivityLogs(UserType userType, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        return userActivityLogRepository.findByUserTypeAndCreateTimeBetween(userType, startTime, endTime, pageable)
                .map(this::convertToDTO);
    }

    @Override
    public List<UserActivityLogDTO> getRecentActivities(UserType userType, int limit) {
        return userActivityLogRepository.findByUserTypeOrderByCreateTimeDesc(userType, Pageable.ofSize(limit))
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private UserActivityLogDTO convertToDTO(UserActivityLog log) {
        UserActivityLogDTO dto = new UserActivityLogDTO();
        dto.setId(log.getId());
        dto.setUserId(log.getUser().getId());
        dto.setUsername(log.getUser().getUsername());
        dto.setUserType(log.getUserType());
        dto.setAction(log.getAction());
        dto.setDescription(log.getDescription());
        dto.setIpAddress(log.getIpAddress());
        dto.setUserAgent(log.getUserAgent());
        dto.setCreateTime(log.getCreateTime());
        return dto;
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
