package com.higame.repository;

import com.higame.entity.ThirdPartyAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ThirdPartyAccountRepository extends JpaRepository<ThirdPartyAccount, Long> {
    List<ThirdPartyAccount> findByUserId(Long userId);
    
    Optional<ThirdPartyAccount> findByPlatformAndThirdPartyUserId(String platform, String thirdPartyUserId);
    
    Optional<ThirdPartyAccount> findByUserIdAndPlatform(Long userId, String platform);
    
    boolean existsByTypeAndThirdPartyUserId(
        ThirdPartyAccount.ThirdPartyType type,
        String thirdPartyUserId
    );
    
    @Modifying
    @Query("UPDATE ThirdPartyAccount a SET a.accessToken = ?2, a.refreshToken = ?3, a.tokenExpireTime = ?4 WHERE a.id = ?1")
    void updateTokenInfo(
        Long accountId,
        String accessToken,
        String refreshToken,
        LocalDateTime tokenExpireTime
    );
    
    @Modifying
    @Query("UPDATE ThirdPartyAccount a SET a.nickname = ?2, a.avatar = ?3, a.email = ?4 WHERE a.id = ?1")
    void updateUserInfo(
        Long accountId,
        String nickname,
        String avatar,
        String email
    );
}