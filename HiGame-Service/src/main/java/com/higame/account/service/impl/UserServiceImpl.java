package com.higame.account.service.impl;

import com.higame.account.dto.RegisterRequest;
import com.higame.account.service.UserService;
import com.higame.dto.LoginRequest;
import com.higame.dto.UserDTO;
import com.higame.entity.User.RegisterType;
import com.higame.entity.*;
import com.higame.repository.UserDeviceRepository;
import com.higame.repository.UserRepository;
import com.higame.service.UserActivityLogService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDeviceRepository userDeviceRepository;

    @Autowired
    private UserActivityLogService userActivityLogService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDTO register(RegisterRequest request) {
        // 验证用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        // 验证邮箱是否已存在（如果提供了邮箱）
        if (StringUtils.isNotEmpty(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("邮箱已被使用");
        }

        // 验证手机号是否已存在（如果提供了手机号）
        if (StringUtils.isNotEmpty(request.getPhone()) && userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("手机号已被使用");
        }

        User user = new User();

        // 设置必填项
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // 设置可选项（带默认值）
        user.setAvatar(StringUtils.isNotEmpty(request.getAvatar()) ?
                request.getAvatar() : "https://api.higame.com/static/default-avatar.png");

        user.setNickname(StringUtils.isNotEmpty(request.getNickname()) ?
                request.getNickname() : "游客" + RandomStringUtils.randomNumeric(6));

        user.setEmail(StringUtils.isNotEmpty(request.getEmail()) ? request.getEmail() : null);
        user.setPhone(StringUtils.isNotEmpty(request.getPhone()) ? request.getPhone() : null);

        user.setRegisterType(RegisterType.SIMPLE);
        user.setUserType(request.getUserType() != null ? request.getUserType() : UserType.APP);
        user.setGender(StringUtils.isNotEmpty(request.getGender()) ? request.getGender() : "unknown");
        user.setBirthday(request.getBirthday());

        // 设置状态和验证标志
        user.setStatus(User.UserStatus.ACTIVE);
        user.setEmailVerified(false);
        user.setPhoneVerified(false);
        user.setTwoFactorEnabled(false);

        // 设置登录相关信息
        user.setLoginFailCount(0);
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp(StringUtils.isNotEmpty(request.getIp()) ?
                request.getIp() : "unknown");

        // 保存用户
        user = userRepository.save(user);

        // 创建用户设备记录（如果提供了设备信息）
        if (StringUtils.isNotEmpty(request.getDeviceId())) {
            UserDevice device = new UserDevice();
            device.setUser(user);
            device.setDeviceId(request.getDeviceId());
            device.setDeviceName(StringUtils.isNotEmpty(request.getDeviceName()) ?
                    request.getDeviceName() : "unknown");
            device.setDeviceModel(StringUtils.isNotEmpty(request.getDeviceModel()) ?
                    request.getDeviceModel() : "unknown");
            device.setOsType(StringUtils.isNotEmpty(request.getOsType()) ?
                    request.getOsType() : "unknown");
            device.setOsVersion(StringUtils.isNotEmpty(request.getOsVersion()) ?
                    request.getOsVersion() : "unknown");
            device.setAppVersion(StringUtils.isNotEmpty(request.getAppVersion()) ?
                    request.getAppVersion() : "1.0.0");
            device.setPushToken(request.getPushToken());
            device.setLastLoginIp(StringUtils.isNotEmpty(request.getIp()) ?
                    request.getIp() : "unknown");
            device.setOnline(true);
            device.setLastActiveTime(LocalDateTime.now());
            userDeviceRepository.save(device);
        }

        // 记录用户活动
        if (request.getRequest() != null) {
            userActivityLogService.logActivity(user, "REGISTER", "用户注册", request.getRequest());
        }

        return convertToDTO(user);
    }

    @Override
    @Transactional
    public UserDTO registerSimple(String username, String password) {
        // 验证用户名是否已存在
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname("游客" + RandomStringUtils.randomNumeric(6));
        user.setAvatar("https://api.higame.com/static/default-avatar.png");
        user.setRegisterType(RegisterType.SIMPLE);
        user.setUserType(UserType.APP);
        user.setGender("unknown");
        user.setStatus(User.UserStatus.ACTIVE);
        user.setEmailVerified(false);
        user.setPhoneVerified(false);
        user.setTwoFactorEnabled(false);
        user.setLoginFailCount(0);
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp("unknown");

        user = userRepository.save(user);
        return convertToDTO(user);
    }

    @Override
    public ResponseEntity<?> login(String username, String password) {
        return null;
    }

    @Transactional
    @Override
    public UserDTO login(LoginRequest request, String ipAddress) {
        // 查找用户
        User user = findUserByCredential(request.getCredential())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 检查用户状态
        checkUserStatus(user);

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            user.setLoginFailCount(user.getLoginFailCount() + 1);
            userRepository.save(user);
            throw new RuntimeException("密码错误");
        }

        // 更新登录信息
        user.setLoginFailCount(0);
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp(ipAddress);
        user = userRepository.save(user);

        // 更新设备信息
        if (StringUtils.isNotEmpty(request.getDeviceId())) {
            UserDevice device = userDeviceRepository.findByUserIdAndDeviceId(user.getId(), request.getDeviceId())
                    .orElseGet(() -> {
                        UserDevice newDevice = new UserDevice();
                        newDevice.setUser(user);
                        newDevice.setDeviceId(request.getDeviceId());
                        return newDevice;
                    });

            device.setDeviceName(StringUtils.isNotEmpty(request.getDeviceName()) ?
                    request.getDeviceName() : "unknown");
            device.setDeviceModel(StringUtils.isNotEmpty(request.getDeviceModel()) ?
                    request.getDeviceModel() : "unknown");
            device.setOsType(StringUtils.isNotEmpty(request.getOsType()) ?
                    request.getOsType() : "unknown");
            device.setOsVersion(StringUtils.isNotEmpty(request.getOsVersion()) ?
                    request.getOsVersion() : "unknown");
            device.setAppVersion(StringUtils.isNotEmpty(request.getAppVersion()) ?
                    request.getAppVersion() : "1.0.0");
            device.setLastLoginIp(ipAddress);
            device.setOnline(true);
            device.setLastActiveTime(LocalDateTime.now());
            userDeviceRepository.save(device);
        }

        return convertToDTO(user);
    }

    @Override
    @Transactional
    public void logout(Long userId, String deviceId) {
        UserDevice device = userDeviceRepository.findByUserIdAndDeviceId(userId, deviceId)
                .orElseThrow(() -> new RuntimeException("设备不存在"));
        device.setOnline(false);
        userDeviceRepository.save(device);
    }

    @Override
    public String refreshToken(String refreshToken) {
        // TODO: 实现刷新令牌的逻辑
        return "";
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void resetPassword(String credential, String verificationCode, String newPassword) {
        User user = findUserByCredential(credential)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // TODO: 验证验证码
        if (!verifyCode("reset", credential, verificationCode)) {
            throw new RuntimeException("验证码错误");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void sendVerificationCode(String type, String target) {
        // TODO: 实现发送验证码的逻辑
    }

    @Override
    public boolean verifyCode(String type, String target, String code) {
        // TODO: 实现验证码验证的逻辑
        return false;
    }

    @Override
    @Transactional
    public UserDTO updateUserInfo(Long userId, UserDTO userDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (StringUtils.isNotEmpty(userDTO.getNickname())) {
            user.setNickname(userDTO.getNickname());
        }
        if (StringUtils.isNotEmpty(userDTO.getAvatar())) {
            user.setAvatar(userDTO.getAvatar());
        }
        if (StringUtils.isNotEmpty(userDTO.getGender())) {
            user.setGender(userDTO.getGender());
        }
        if (userDTO.getBirthday() != null) {
            user.setBirthday(userDTO.getBirthday());
        }

        user = userRepository.save(user);
        return convertToDTO(user);
    }

    @Override
    public UserDTO getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return convertToDTO(user);
    }

    @Override
    public List<UserDevice> getUserDevices(Long userId) {
        return userDeviceRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public void logoutOtherDevices(Long userId, String currentDeviceId) {
        List<UserDevice> devices = userDeviceRepository.findByUserId(userId);
        for (UserDevice device : devices) {
            if (!device.getDeviceId().equals(currentDeviceId)) {
                device.setOnline(false);
                userDeviceRepository.save(device);
            }
        }
    }

    @Override
    @Transactional
    public void toggleTwoFactorAuth(Long userId, boolean enable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setTwoFactorEnabled(enable);
        userRepository.save(user);
    }


    @Override
    public ResponseEntity<?> bindThirdParty(String platform, String authCode) {
        return null;
    }

    @Override
    public void updateUserStatus(Long userId, User.UserStatus status, String reason, Integer banDays) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        user.setStatus(status);
        if (status == User.UserStatus.BANNED) {
            if (banDays == null || banDays <= 0) {
                throw new RuntimeException("封禁天数必须大于0");
            }
            user.setBanReason(reason);
            user.setBanExpireTime(LocalDateTime.now().plusDays(banDays));
        }

        userRepository.save(user);
    }

    @Transactional
    @Override
    public void bindThirdPartyAccount(Long userId, String platform, String thirdPartyId) {
        // TODO: 实现第三方账号绑定的逻辑
    }

    @Override
    @Transactional
    public void unbindThirdPartyAccount(Long userId, String type) {
        // TODO: 实现第三方账号解绑的逻辑
    }

    @Override
    @Transactional
    public UserDTO loginWithThirdParty(String type, String thirdPartyToken, String deviceId) {
        // TODO: 实现第三方登录的逻辑
        return null;
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    @Override
    public User getUserByPhone(String phone) {
        return userRepository.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    @Override
    public void updateUser(User user) {
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setNickname(user.getNickname());
        dto.setAvatar(user.getAvatar());
        dto.setGender(user.getGender());
        dto.setBirthday(user.getBirthday());
        dto.setStatus(user.getStatus());
        dto.setEmailVerified(user.isEmailVerified());
        dto.setPhoneVerified(user.isPhoneVerified());
        dto.setCreateTime(user.getCreateTime());
        dto.setUpdateTime(user.getUpdateTime());
        return dto;
    }

    private Optional<User> findUserByCredential(String credential) {
        Optional<User> user = userRepository.findByUsername(credential);
        if (user.isEmpty()) {
            user = userRepository.findByEmail(credential);
        }
        if (user.isEmpty()) {
            user = userRepository.findByPhone(credential);
        }
        return user;
    }

    private void checkUserStatus(User user) {
        if (user.getStatus() == User.UserStatus.BANNED) {
            if (user.getBanExpireTime() != null && LocalDateTime.now().isBefore(user.getBanExpireTime())) {
                throw new RuntimeException(String.format("账号已被封禁，原因：%s，解封时间：%s",
                        user.getBanReason(), user.getBanExpireTime()));
            } else {
                user.setStatus(User.UserStatus.ACTIVE);
                user.setBanReason(null);
                user.setBanExpireTime(null);
                userRepository.save(user);
            }
        } else if (user.getStatus() == User.UserStatus.DELETED) {
            throw new RuntimeException("账号已被删除");
        }
    }
}