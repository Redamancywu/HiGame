package com.higame.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
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

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phone;

    private String password;

    private String nickname;

    private String avatar;

    private String gender;

    private LocalDateTime birthday;

    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE;

    private String banReason;

    private LocalDateTime banExpireTime;

    private boolean emailVerified;

    private boolean phoneVerified;

    private boolean twoFactorEnabled;

    private String twoFactorSecret;

    private String securityQuestion;

    private String securityAnswer;

    private int loginFailCount;

    private LocalDateTime lastLoginTime;

    private String lastLoginIp;

    @CreationTimestamp
    private LocalDateTime createTime;

    @UpdateTimestamp
    private LocalDateTime updateTime;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<UserDevice> devices;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<ThirdPartyAccount> thirdPartyAccounts;

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
        return status != UserStatus.BANNED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status == UserStatus.ACTIVE;
    }

    public enum UserStatus {
        ACTIVE,
        BANNED,
        DISABLED
    }
}