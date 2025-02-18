package com.higame.repository;

import com.higame.entity.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {
    List<UserDevice> findByUserId(Long userId);
    
    Optional<UserDevice> findByUserIdAndDeviceId(Long userId, String deviceId);
    
    Optional<UserDevice> findByRefreshToken(String refreshToken);
    
    @Modifying
    @Query("UPDATE UserDevice d SET d.isOnline = true, d.lastActiveTime = ?2, d.lastLoginIp = ?3, d.refreshToken = ?4 WHERE d.id = ?1")
    void updateDeviceLogin(Long deviceId, LocalDateTime lastActiveTime, String lastLoginIp, String refreshToken);
    
    @Modifying
    @Query("UPDATE UserDevice d SET d.isOnline = false WHERE d.id = ?1")
    void updateDeviceLogout(Long deviceId);
    
    @Modifying
    @Query("UPDATE UserDevice d SET d.isOnline = false WHERE d.user.id = ?1 AND d.id != ?2")
    void logoutOtherDevices(Long userId, Long currentDeviceId);
    
    @Modifying
    @Query("UPDATE UserDevice d SET d.pushToken = ?2 WHERE d.id = ?1")
    void updatePushToken(Long deviceId, String pushToken);
}