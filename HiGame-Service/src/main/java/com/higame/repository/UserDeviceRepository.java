package com.higame.repository;

import com.higame.entity.UserDevice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {
    Optional<UserDevice> findByDeviceId(String deviceId);
    List<UserDevice> findByUserId(Long userId);
    Optional<UserDevice> findByUserIdAndDeviceId(Long userId, String deviceId);
    Optional<UserDevice> findByRefreshToken(String refreshToken);
    boolean existsByDeviceId(String deviceId);
    
    @Query("SELECT ud FROM UserDevice ud WHERE ud.user.id = :userId AND ud.online = true")
    List<UserDevice> findOnlineDevicesByUserId(@Param("userId") Long userId);
    
    @Modifying
    @Query("UPDATE UserDevice ud SET ud.online = :online, ud.lastActiveTime = :activeTime WHERE ud.id = :deviceId")
    void updateOnlineStatus(@Param("deviceId") Long deviceId, 
                           @Param("online") boolean online, 
                           @Param("activeTime") LocalDateTime activeTime);
    
    @Modifying
    @Query("UPDATE UserDevice ud SET ud.refreshToken = :token WHERE ud.id = :deviceId")
    void updateRefreshToken(@Param("deviceId") Long deviceId, @Param("token") String token);
    
    Page<UserDevice> findByUserIdOrderByLastActiveTimeDesc(Long userId, Pageable pageable);
    
    void deleteByLastActiveTimeBefore(LocalDateTime dateTime);

    @Query("SELECT COUNT(d) FROM UserDevice d WHERE d.online = true")
    long countByOnlineTrue();
}