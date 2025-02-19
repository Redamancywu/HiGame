package com.higame.repository;

import com.higame.entity.UserActivityLog;
import com.higame.entity.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserActivityLogRepository extends JpaRepository<UserActivityLog, Long> {
    Page<UserActivityLog> findByUserTypeAndCreateTimeBetween(
            UserType userType, 
            LocalDateTime startTime, 
            LocalDateTime endTime, 
            Pageable pageable
    );
    
    List<UserActivityLog> findByUserTypeOrderByCreateTimeDesc(UserType userType, Pageable pageable);
    
    void deleteByCreateTimeBefore(LocalDateTime dateTime);
}
