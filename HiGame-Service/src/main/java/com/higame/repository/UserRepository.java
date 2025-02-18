package com.higame.repository;

import com.higame.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByPhone(String phone);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    boolean existsByPhone(String phone);
    
    @Modifying
    @Query("UPDATE User u SET u.loginFailCount = u.loginFailCount + 1 WHERE u.id = ?1")
    void incrementLoginFailCount(Long userId);
    
    @Modifying
    @Query("UPDATE User u SET u.loginFailCount = 0, u.lastLoginTime = ?2, u.lastLoginIp = ?3 WHERE u.id = ?1")
    void updateLoginSuccess(Long userId, LocalDateTime lastLoginTime, String lastLoginIp);
    
    @Modifying
    @Query("UPDATE User u SET u.status = ?2, u.banReason = ?3, u.banExpireTime = ?4 WHERE u.id = ?1")
    void updateUserStatus(Long userId, User.UserStatus status, String banReason, LocalDateTime banExpireTime);
}