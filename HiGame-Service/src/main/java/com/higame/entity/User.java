package com.higame.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@Data
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phone;

    @Column(nullable = false)
    private String password;

    private String nickname;

    private String avatar;

    @Column(name = "register_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private RegisterType registerType = RegisterType.SIMPLE;

    @Column(name = "user_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType = UserType.APP;

    private String gender;

    private LocalDateTime birthday;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "ban_reason")
    private String banReason;

    @Column(name = "ban_expire_time")
    private LocalDateTime banExpireTime;

    @Column(name = "email_verified")
    private boolean emailVerified = false;

    @Column(name = "phone_verified")
    private boolean phoneVerified = false;

    @Column(name = "two_factor_enabled")
    private boolean twoFactorEnabled = false;

    @Column(name = "two_factor_secret")
    private String twoFactorSecret;

    @Column(name = "security_question")
    private String securityQuestion;

    @Column(name = "security_answer")
    private String securityAnswer;

    @Column(name = "login_fail_count")
    private Integer loginFailCount = 0;

    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;

    @Column(name = "last_login_ip")
    private String lastLoginIp;

    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<UserDevice> devices;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<ThirdPartyAccount> thirdPartyAccounts;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }

    public enum UserStatus {
        ACTIVE(0, "active"),
        BANNED(1, "banned"),
        DELETED(2, "deleted");

        private final int code;
        private final String desc;

        UserStatus(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

    public enum RegisterType {
        SIMPLE,
        EMAIL,
        PHONE,
        THIRD_PARTY
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status != UserStatus.BANNED || 
               (banExpireTime != null && LocalDateTime.now().isAfter(banExpireTime));
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status == UserStatus.ACTIVE;
    }
}