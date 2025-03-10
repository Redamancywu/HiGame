package com.higame.repository;

import com.higame.entity.User;
import com.higame.entity.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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
    
    long countByUserType(UserType userType);
    
    @Query("SELECT COUNT(u) FROM User u WHERE DATE(u.lastLoginTime) = :date")
    long countActiveUsersByDate(LocalDate date);
    
    @Query("SELECT COUNT(u) FROM User u WHERE DATE(u.createTime) = :date")
    long countNewUsersByDate(LocalDate date);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.userType = :userType AND DATE(u.lastLoginTime) = :date")
    Integer countActiveUsersByType(@Param("userType") UserType userType, @Param("date") LocalDate date);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.userType = :userType AND DATE(u.createTime) = :date")
    Integer countNewUsersByType(@Param("userType") UserType userType, @Param("date") LocalDate date);
    
    @Modifying
    @Query("UPDATE User u SET u.status = :status, u.banReason = :reason, u.banExpireTime = :expireTime WHERE u.id = :userId")
    void updateUserStatus(@Param("userId") Long userId, 
                         @Param("status") User.UserStatus status, 
                         @Param("reason") String reason, 
                         @Param("expireTime") LocalDateTime expireTime);
    
    @Modifying
    @Query("UPDATE User u SET u.lastLoginTime = :loginTime, u.lastLoginIp = :loginIp, u.loginFailCount = 0 WHERE u.id = :userId")
    void updateLoginSuccess(@Param("userId") Long userId, 
                          @Param("loginTime") LocalDateTime loginTime, 
                          @Param("loginIp") String loginIp);
    
    @Modifying
    @Query("UPDATE User u SET u.lastLoginTime = :loginTime, u.lastLoginIp = :loginIp WHERE u.id = :userId")
    void updateLastLogin(@Param("userId") Long userId, 
                        @Param("loginTime") LocalDateTime loginTime, 
                        @Param("loginIp") String loginIp);
    
    @Modifying
    @Query("UPDATE User u SET u.loginFailCount = :count WHERE u.id = :userId")
    void updateLoginFailCount(@Param("userId") Long userId, @Param("count") Integer count);

    @Query("SELECT u FROM User u WHERE " +
           "(:query IS NULL OR u.username LIKE %:query% OR u.email LIKE %:query% OR u.phone LIKE %:query%)")
    Page<User> findByQuery(@Param("query") String query, Pageable pageable);

    Page<User> findByUsernameContainingOrNicknameContaining(
        String username, String nickname, Pageable pageable);
}