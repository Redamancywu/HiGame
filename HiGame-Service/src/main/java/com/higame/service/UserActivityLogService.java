package com.higame.service;

import com.higame.dto.UserActivityLogDTO;
import com.higame.entity.User;
import com.higame.entity.UserType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

public interface UserActivityLogService {
    void logActivity(User user, String action, String description, HttpServletRequest request);



    Page<UserActivityLogDTO> getActivityLogs(UserType userType, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    List<UserActivityLogDTO> getRecentActivities(UserType userType, int limit);
}
