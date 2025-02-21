package com.higame.admin.service.impl;

import com.higame.admin.dto.UserCreateRequest;
import com.higame.admin.dto.UserUpdateRequest;
import com.higame.admin.dto.UserStatusRequest;
import com.higame.admin.service.AdminUserService;
import com.higame.entity.User;
import com.higame.entity.UserType;
import com.higame.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<?> getUserList(int page, int size, String query) {
        try {
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id").descending());
            Page<User> userPage;
            
            if (query != null && !query.trim().isEmpty()) {
                userPage = userRepository.findByUsernameContainingOrNicknameContaining(
                    query, query, pageRequest);
            } else {
                userPage = userRepository.findAll(pageRequest);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("content", userPage.getContent());
            response.put("totalElements", userPage.getTotalElements());
            response.put("totalPages", userPage.getTotalPages());
            response.put("currentPage", userPage.getNumber());
            response.put("size", userPage.getSize());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取用户列表失败", e);
            throw new RuntimeException("获取用户列表失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> createUser(UserCreateRequest request) {
        // 验证用户名是否存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setUserType(UserType.valueOf(request.getUserType()));
        user.setStatus(User.UserStatus.ACTIVE);

        user = userRepository.save(user);
        return ResponseEntity.ok(user);
    }

    @Override
    @Transactional
    public ResponseEntity<?> updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        user = userRepository.save(user);
        return ResponseEntity.ok(user);
    }

    @Override
    @Transactional
    public ResponseEntity<?> deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("用户不存在");
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Override
    @Transactional
    public ResponseEntity<?> updateUserStatus(Long id, UserStatusRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        user.setStatus(User.UserStatus.valueOf(request.getStatus()));
        user.setBanReason(request.getReason());
        user.setBanExpireTime(request.getExpireTime());

        userRepository.save(user);
        return ResponseEntity.ok().build();
    }
} 