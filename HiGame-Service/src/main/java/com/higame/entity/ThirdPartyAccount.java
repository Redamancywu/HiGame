package com.higame.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "third_party_accounts")
public class ThirdPartyAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private ThirdPartyType type;

    @Column(name = "platform")
    private String platform;

    @Column(unique = true)
    private String thirdPartyUserId;

    private String accessToken;

    private String refreshToken;

    private LocalDateTime tokenExpireTime;

    private String nickname;

    private String avatar;

    private String email;

    @CreationTimestamp
    private LocalDateTime createTime;

    @UpdateTimestamp
    private LocalDateTime updateTime;

    public enum ThirdPartyType {
        WECHAT,
        QQ,
        GOOGLE,
        FACEBOOK
    }
}