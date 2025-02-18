package com.higame.account.service.impl;

import com.higame.dto.LoginRequest;
import com.higame.dto.RegisterRequest;
import com.higame.dto.UserDTO;
import com.higame.entity.User;
import com.higame.entity.User.UserStatus;
import com.higame.entity.UserDevice;
import com.higame.entity.ThirdPartyAccount;
import com.higame.repository.UserRepository;
import com.higame.repository.UserDeviceRepository;
import com.higame.repository.ThirdPartyAccountRepository;
import com.higame.account.service.UserService;
import com.higame.service.JwtService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserDeviceRepository userDeviceRepository;
    
    @Autowired
    private ThirdPartyAccountRepository thirdPartyAccountRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOGIN_LOCK_DURATION = 30; // 分钟
    
    @Override
    @Transactional
    public UserDTO register(RegisterRequest request) {
        // 验证用户名、邮箱、手机号是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("邮箱已被注册");
        }
        if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("手机号已被注册");
        }
        
        // 验证验证码
        if (request.getEmail() != null) {
            verifyCode("register_email", request.getEmail(), request.getVerificationCode());
        }
        if (request.getPhone() != null) {
            verifyCode("register_phone", request.getPhone(), request.getVerificationCode());
        }
        
        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setNickname(request.getNickname());
        user.setStatus(User.UserStatus.ACTIVE);
        user = userRepository.save(user);
        
        // 创建设备记录
        if (request.getDeviceId() != null) {
            UserDevice device = new UserDevice();
            device.setUser(user);
            device.setDeviceId(request.getDeviceId());
            device.setDeviceName(request.getDeviceName());
            device.setDeviceModel(request.getDeviceModel());
            device.setOsType(request.getOsType());
            device.setOsVersion(request.getOsVersion());
            device.setAppVersion(request.getAppVersion());
            device.setIsOnline(true);
            device.setLastActiveTime(LocalDateTime.now());
            userDeviceRepository.save(device);
        }
        
        return convertToDTO(user);
    }

    @Transactional
    public UserDTO registerSimple(String username, String password) {
        // 验证用户名是否已存在
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 创建用户
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setStatus(User.UserStatus.ACTIVE);
        user.setRegisterType(User.RegisterType.SIMPLE);
        user = userRepository.save(user);
        
        return convertToDTO(user);
    }

    @Override
    @Transactional
    public UserDTO login(LoginRequest request, String ipAddress) {
        // 查找用户
        User user = findUserByCredential(request.getCredential())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 检查用户状态
        checkUserStatus(user);
        
        // 检查登录失败次数
        String loginFailKey = "login_fail:" + user.getId();
        Integer failCount = Integer.valueOf(redisTemplate.opsForValue().get(loginFailKey) == null ? "0" : redisTemplate.opsForValue().get(loginFailKey));
        if (failCount >= MAX_LOGIN_ATTEMPTS) {
            throw new RuntimeException("登录失败次数过多，请30分钟后再试");
        }
        
        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            redisTemplate.opsForValue().increment(loginFailKey);
            redisTemplate.expire(loginFailKey, LOGIN_LOCK_DURATION, TimeUnit.MINUTES);
            throw new RuntimeException("密码错误");
        }
        
        // 验证双重认证
        if (user.isTwoFactorEnabled()) {
            if (request.getVerificationCode() == null) {
                throw new RuntimeException("需要验证码");
            }
            verifyCode("2fa", user.getId().toString(), request.getVerificationCode());
        }
        
        // 更新登录信息
        userRepository.updateLoginSuccess(user.getId(), LocalDateTime.now(), ipAddress);
        redisTemplate.delete(loginFailKey);
        
        // 更新设备信息
        final UserDevice device = userDeviceRepository.findByUserIdAndDeviceId(user.getId(), request.getDeviceId())
                .orElseGet(() -> {
                    UserDevice newDevice = new UserDevice();
                    newDevice.setUser(user);
                    newDevice.setDeviceId(request.getDeviceId());
                    newDevice.setDeviceName(request.getDeviceName());
                    newDevice.setDeviceModel(request.getDeviceModel());
                    newDevice.setOsType(request.getOsType());
                    newDevice.setOsVersion(request.getOsVersion());
                    newDevice.setAppVersion(request.getAppVersion());
                    return newDevice;
                });
        
        String refreshToken = jwtService.generateRefreshToken(user);
        device.setRefreshToken(refreshToken);
        device.setLastActiveTime(LocalDateTime.now());
        device.setLastLoginIp(ipAddress);
        device.setIsOnline(true);
        userDeviceRepository.save(device);
        
        return convertToDTO(user);
    }
    
    @Override
    @Transactional
    public void logout(Long userId, String deviceId) {
        UserDevice device = userDeviceRepository.findByUserIdAndDeviceId(userId, deviceId)
                .orElseThrow(() -> new RuntimeException("设备不存在"));
        device.setIsOnline(false);
        device.setRefreshToken(null);
        userDeviceRepository.save(device);
    }
    
    @Override
    public String refreshToken(String refreshToken) {
        UserDevice device = userDeviceRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("无效的刷新令牌"));
        
        if (!device.isOnline()) {
            throw new RuntimeException("设备已下线");
        }
        
        return jwtService.generateAccessToken(device.getUser());
    }
    
    @Override
    public void sendVerificationCode(String type, String target) {
        String code = generateVerificationCode();
        String key = String.format("verification:%s:%s", type, target);
        redisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES);
        
        // TODO: 实现具体的发送逻辑（短信或邮件）
    }
    
    @Override
    public boolean verifyCode(String type, String target, String code) {
        if (code == null) {
            return false;
        }
        
        String key = String.format("verification:%s:%s", type, target);
        String savedCode = redisTemplate.opsForValue().get(key);
        
        if (savedCode != null && savedCode.equals(code)) {
            redisTemplate.delete(key);
            return true;
        }
        return false;
    }
    
    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
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
            if (user.getBanExpireTime() != null && user.getBanExpireTime().isAfter(LocalDateTime.now())) {
                throw new RuntimeException(String.format("账号已被封禁，原因：%s，解封时间：%s",
                        user.getBanReason(), user.getBanExpireTime()));
            } else {
                user.setStatus(User.UserStatus.ACTIVE);
                user.setBanReason(null);
                user.setBanExpireTime(null);
                userRepository.save(user);
            }
        } else if (user.getStatus() == User.UserStatus.DISABLED) {
            throw new RuntimeException("账号已被禁用");
        }
    }
    
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto);
        return dto;
    }
    
    @Override
    @Transactional
    public void bindThirdPartyAccount(Long userId, String platform, String thirdPartyId) {
        // 验证用户是否存在
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 检查该第三方账号是否已被其他用户绑定
        Optional<ThirdPartyAccount> existingAccount = thirdPartyAccountRepository
                .findByPlatformAndThirdPartyUserId(platform, thirdPartyId);
        if (existingAccount.isPresent() && !existingAccount.get().getUser().getId().equals(userId)) {
            throw new RuntimeException("该第三方账号已被其他用户绑定");
        }
        
        // 检查用户是否已绑定该平台的账号
        Optional<ThirdPartyAccount> userAccount = thirdPartyAccountRepository
                .findByUserIdAndPlatform(userId, platform);
        
        ThirdPartyAccount account;
        if (userAccount.isPresent()) {
            // 更新已有绑定
            account = userAccount.get();
            account.setThirdPartyUserId(thirdPartyId);
        } else {
            // 创建新绑定
            account = new ThirdPartyAccount();
            account.setUser(user);
            account.setType(ThirdPartyAccount.ThirdPartyType.valueOf(platform));
            account.setThirdPartyUserId(thirdPartyId);
        }
        
        thirdPartyAccountRepository.save(account);
    }
    
    @Override
    public UserDTO getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return convertToDTO(user);
    }

    @Override
    @Transactional
    public void logoutOtherDevices(Long userId, String currentDeviceId) {
        List<UserDevice> devices = userDeviceRepository.findByUserId(userId);
        for (UserDevice device : devices) {
            if (!device.getDeviceId().equals(currentDeviceId)) {
                device.setIsOnline(false);
                device.setRefreshToken(null);
            }
        }
        userDeviceRepository.saveAll(devices);
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

        // 修改密码后，登出所有设备
        List<UserDevice> devices = userDeviceRepository.findByUserId(userId);
        for (UserDevice device : devices) {
            device.setIsOnline(false);
            device.setRefreshToken(null);
        }
        userDeviceRepository.saveAll(devices);
    }

    @Override
    @Transactional
    public void resetPassword(String credential, String verificationCode, String newPassword) {
        // 验证用户是否存在
        User user = findUserByCredential(credential)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 验证验证码
        String type = credential.contains("@") ? "reset_email" : "reset_phone";
        if (!verifyCode(type, credential, verificationCode)) {
            throw new RuntimeException("验证码无效");
        }
        
        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        // 登出所有设备
        List<UserDevice> devices = userDeviceRepository.findByUserId(user.getId());
        for (UserDevice device : devices) {
            device.setIsOnline(false);
            device.setRefreshToken(null);
        }
        userDeviceRepository.saveAll(devices);
    }

    @Override
    @Transactional
    public UserDTO updateUserInfo(Long userId, UserDTO userDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 更新基本信息
        if (userDTO.getNickname() != null) {
            user.setNickname(userDTO.getNickname());
        }
        if (userDTO.getAvatar() != null) {
            user.setAvatar(userDTO.getAvatar());
        }
        if (userDTO.getGender() != null) {
            user.setGender(userDTO.getGender());
        }
        if (userDTO.getBirthday() != null) {
            user.setBirthday(userDTO.getBirthday());
        }
        
        // 更新敏感信息（需要验证码）
        if (userDTO.getEmail() != null && !userDTO.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(userDTO.getEmail())) {
                throw new RuntimeException("邮箱已被使用");
            }
            verifyCode("update_email", userDTO.getEmail(), userDTO.getVerificationCode());
            user.setEmail(userDTO.getEmail());
        }
        if (userDTO.getPhone() != null && !userDTO.getPhone().equals(user.getPhone())) {
            if (userRepository.existsByPhone(userDTO.getPhone())) {
                throw new RuntimeException("手机号已被使用");
            }
            verifyCode("update_phone", userDTO.getPhone(), userDTO.getVerificationCode());
            user.setPhone(userDTO.getPhone());
        }
        
        return convertToDTO(userRepository.save(user));
    }

    @Override
    public List<UserDevice> getUserDevices(Long userId) {
        return userDeviceRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public void toggleTwoFactorAuth(Long userId, boolean enable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 检查是否有可用的验证方式（邮箱或手机号）
        if (enable && user.getEmail() == null && user.getPhone() == null) {
            throw new RuntimeException("需要先绑定邮箱或手机号才能开启双重认证");
        }
        
        user.setTwoFactorEnabled(enable);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUserStatus(Long userId, UserStatus status, String reason, Integer banDays) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        user.setStatus(status);
        
        if (status == UserStatus.BANNED) {
            if (banDays == null || banDays <= 0) {
                throw new RuntimeException("封禁天数必须大于0");
            }
            if (reason == null || reason.trim().isEmpty()) {
                throw new RuntimeException("必须提供封禁原因");
            }
            user.setBanReason(reason);
            user.setBanExpireTime(LocalDateTime.now().plusDays(banDays));
            
            // 登出所有设备
            List<UserDevice> devices = userDeviceRepository.findByUserId(userId);
            for (UserDevice device : devices) {
                device.setIsOnline(false);
                device.setRefreshToken(null);
            }
            userDeviceRepository.saveAll(devices);
        } else if (status == UserStatus.ACTIVE) {
            user.setBanReason(null);
            user.setBanExpireTime(null);
        }
        
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void unbindThirdPartyAccount(Long userId, String type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        ThirdPartyAccount account = thirdPartyAccountRepository
                .findByUserIdAndPlatform(userId, type)
                .orElseThrow(() -> new RuntimeException("未绑定该平台账号"));
        
        thirdPartyAccountRepository.delete(account);
    }

    @Override
    @Transactional
    public UserDTO loginWithThirdParty(String type, String thirdPartyToken, String deviceId) {
        // TODO: 实现第三方平台的token验证，获取用户信息
        // 这里需要根据不同平台（如微信、QQ、Google等）实现具体的验证逻辑
        String thirdPartyUserId = "demo_user_id"; // 这里应该是从第三方平台获取的用户ID
        
        // 查找是否已有绑定记录
        Optional<ThirdPartyAccount> existingAccount = thirdPartyAccountRepository
                .findByPlatformAndThirdPartyUserId(type, thirdPartyUserId);
        
        User user;
        if (existingAccount.isPresent()) {
            // 已绑定账号，直接登录
            user = existingAccount.get().getUser();
        } else {
            // 创建新用户并绑定
            user = new User();
            user.setUsername("user_" + System.currentTimeMillis()); // 生成随机用户名
            user.setStatus(UserStatus.ACTIVE);
            user = userRepository.save(user);
            
            // 创建第三方账号绑定
            ThirdPartyAccount account = new ThirdPartyAccount();
            account.setUser(user);
            account.setType(ThirdPartyAccount.ThirdPartyType.valueOf(type));
            account.setThirdPartyUserId(thirdPartyUserId);
            thirdPartyAccountRepository.save(account);
        }
        
        // 更新设备信息
        if (deviceId != null) {
            // 将user对象存储为final变量以在lambda表达式中使用
            final User finalUser = user;
            UserDevice device = userDeviceRepository.findByUserIdAndDeviceId(finalUser.getId(), deviceId)
                    .orElseGet(() -> {
                        UserDevice newDevice = new UserDevice();
                        newDevice.setUser(finalUser);
                        newDevice.setDeviceId(deviceId);
                        return newDevice;
                    });
            
            String refreshToken = jwtService.generateRefreshToken(finalUser);
            device.setRefreshToken(refreshToken);
            device.setLastActiveTime(LocalDateTime.now());
            device.setIsOnline(true);
            userDeviceRepository.save(device);
        }
        
        return convertToDTO(user);
    }

    // 其他接口实现...
}