package org.example.studyhub.service.impl;

import jakarta.transaction.Transactional;
import org.example.studyhub.dto.UserDTO;
import org.example.studyhub.mapper.UserMapper;
import org.example.studyhub.model.User;
import org.example.studyhub.model.UserRole;
import org.example.studyhub.repository.SettingRepository;
import org.example.studyhub.repository.UserRepository;
import org.example.studyhub.service.EmailService;
import org.example.studyhub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.example.studyhub.model.Setting;
import java.util.HashSet;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SettingRepository settingRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;

    @Override
    public Page<UserDTO> searchUsers(Long roleId, String status, String keyword, int page, int size) {

        String normalizedStatus = (status != null && !status.trim().isEmpty())
                ? status.trim().toUpperCase() : null;

        String normalizedKeyword = (keyword != null && !keyword.trim().isEmpty())
                ? keyword.trim().toLowerCase() : "";

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<User> userPage = userRepository.searchUsers(normalizedStatus, roleId, normalizedKeyword, pageable);
        return userPage.map(userMapper::toDTO);
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
        UserDTO dto = userMapper.toDTO(user);

        if (user.getUserRoles() != null) {
            List<Long> currentRoleIds = user.getUserRoles().stream()
                    .map(ur -> ur.getRole().getId()) // Lấy ID của Setting
                    .toList();
            dto.setRoleIds(currentRoleIds);

            List<String> currentRoleNames = user.getUserRoles().stream()
                    .map(ur -> ur.getRole().getName())
                    .toList();
            dto.setRoles(currentRoleNames);
        }
        return dto;
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        if (userDTO.getFullName() == null || userDTO.getFullName().trim().length() < 2 || userDTO.getFullName().trim().length() > 20 ) {
            throw new IllegalArgumentException("Full name must be 3-20 characters long.");
        }

        if (userDTO.getUsername() == null || !userDTO.getUsername().matches("^[a-zA-Z0-9_]{3,20}$")) {
            throw new IllegalArgumentException("Username must be 3-20 characters long and contain only letters, numbers, and underscores.");
        }

        if (userDTO.getEmail() == null || !userDTO.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format.");
        }

        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("This email is already in use. Please choose another one.");
        }
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new IllegalArgumentException("This username already exists. Please choose another one.");
        }

        User user = userMapper.toEntity(userDTO);
        String rawPassword = generateRandomPassword(8);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setStatus("UNVERIFIED");

        user = userRepository.save(user);
        try {
            emailService.sendNewAccountEmail(user.getEmail(), user.getUsername(), rawPassword);
        } catch (Exception e) {
            System.err.println("Lỗi khi gửi email: " + e.getMessage());
        }
        return userMapper.toDTO(user);
    }

    private String generateRandomPassword(int length) {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));

        if (userDTO.getFullName() == null || userDTO.getFullName().trim().length() < 2 || userDTO.getFullName().trim().length() > 20 ) {
            throw new IllegalArgumentException("Full name must be 3-20 characters long.");
        }

        if (userDTO.getUsername() == null || !userDTO.getUsername().matches("^[a-zA-Z0-9_]{3,20}$")) {
            throw new IllegalArgumentException("Username must be 3-20 characters long and contain only letters, numbers, and underscores.");
        }
        if (!existingUser.getUsername().equals(userDTO.getUsername()) && userRepository.existsByUsername(userDTO.getUsername())) {
            throw new IllegalArgumentException("This username already exists. Please choose another one.");
        }

        if (userDTO.getEmail() == null || !userDTO.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        if (!existingUser.getEmail().equals(userDTO.getEmail()) && userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("This email is already in use. Please choose another one.");
        }

        existingUser.setFullName(userDTO.getFullName());
        existingUser.setUsername(userDTO.getUsername());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setStatus(userDTO.getStatus());

        existingUser.getUserRoles().clear();

        if (userDTO.getRoleIds() != null && !userDTO.getRoleIds().isEmpty()) {
            for (Long roleId : userDTO.getRoleIds()) {
                settingRepository.findById(roleId).ifPresent(roleSetting -> {
                    UserRole userRole = new UserRole();
                    userRole.setId(new UserRole.UserRoleId(existingUser.getId(), roleId)); // Set khóa chính kép
                    userRole.setUser(existingUser);
                    userRole.setRole(roleSetting);
                    existingUser.getUserRoles().add(userRole);
                });
            }
        }

        userRepository.save(existingUser);
        return userMapper.toDTO(existingUser);
    }

    @Override
    public void changeStatus(Long id, String status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));

        user.setStatus(status.toUpperCase());
        userRepository.save(user);
    }

    @Override
    public User authenticate(String email, String password) {
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return null;
        }
        if (passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        else if (password.equals(user.getPassword())) {

            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);

            System.out.println("Đã tự động mã hóa mật khẩu cho user: " + email);
            return user;
        }

        return null;
    }

    @Override
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản với email này."));

        if (!"UNVERIFIED".equals(user.getStatus())) {
            throw new IllegalArgumentException("Tài khoản này đã được xác thực hoặc bị khóa.");
        }

        String token = UUID.randomUUID().toString();
        user.setEmailVerifyToken(token);
        user.setEmailVerifyExpiredAt(LocalDateTime.now().plusHours(24));
        userRepository.save(user);

        // Gửi email
        emailService.sendVerificationEmail(user.getEmail(), token);
    }

    @Override
    public boolean validateVerificationToken(String token) {
        User user = userRepository.findByEmailVerifyToken(token).orElse(null);
        if (user == null) return false;

        return user.getEmailVerifyExpiredAt() != null && !user.getEmailVerifyExpiredAt().isBefore(LocalDateTime.now());
    }

    @Override
    public void verifyAndSetupPassword(String token, String newPassword) {
        User user = userRepository.findByEmailVerifyToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token không hợp lệ hoặc không tồn tại."));

        if (user.getEmailVerifyExpiredAt() == null || user.getEmailVerifyExpiredAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Đường link xác thực đã hết hạn.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setStatus("ACTIVE");

        user.setEmailVerifyToken(null);
        user.setEmailVerifyExpiredAt(null);

        userRepository.save(user);
    }
    @Override
    public void registerByEmail(String fullName, String email, String rawPassword) {
        String normalizedFullName = fullName == null ? "" : fullName.trim();
        String normalizedEmail = email == null ? "" : email.trim().toLowerCase();

        if (normalizedFullName.length() < 2) {
            throw new IllegalArgumentException("Họ tên phải có ít nhất 2 ký tự.");
        }
        if (!normalizedEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Email không hợp lệ.");
        }
        if (rawPassword == null || rawPassword.length() < 6) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 6 ký tự.");
        }
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("Email đã được sử dụng.");
        }

        String username = buildUniqueUsernameFromEmail(normalizedEmail);

        User user = new User();
        user.setFullName(normalizedFullName);
        user.setEmail(normalizedEmail);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setStatus("UNVERIFIED");
        user.setEmailVerifyToken(UUID.randomUUID().toString());
        user.setEmailVerifyExpiredAt(LocalDateTime.now().plusHours(24));
        user.setUserRoles(new HashSet<>());

        user = userRepository.save(user);

        Setting memberRole = settingRepository.findByTypeIdAndStatus(1L, "ACTIVE").stream()
                .filter(s -> "MEMBER".equalsIgnoreCase(s.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Chưa có role MEMBER trong bảng setting."));

        UserRole userRole = new UserRole();
        userRole.setId(new UserRole.UserRoleId(user.getId(), memberRole.getId()));
        userRole.setUser(user);
        userRole.setRole(memberRole);

        user.getUserRoles().add(userRole);
        userRepository.save(user);

        emailService.sendVerificationEmail(user.getEmail(), user.getEmailVerifyToken());
    }

    private String buildUniqueUsernameFromEmail(String email) {
        String localPart = email.substring(0, email.indexOf('@'));
        String base = localPart.replaceAll("[^a-zA-Z0-9_]", "_");
        if (base.isBlank()) {
            base = "user";
        }
        if (base.length() > 20) {
            base = base.substring(0, 20);
        }

        String candidate = base;
        int suffix = 1;
        while (userRepository.existsByUsername(candidate)) {
            String s = "_" + suffix++;
            int maxBaseLen = Math.max(1, 20 - s.length());
            String cut = base.length() > maxBaseLen ? base.substring(0, maxBaseLen) : base;
            candidate = cut + s;
        }
        return candidate;
    }
    @Override
    public boolean verifyEmailToken(String token) {
        User user = userRepository.findByEmailVerifyToken(token).orElse(null);
        if (user == null) return false;

        if (user.getEmailVerifyExpiredAt() == null
                || user.getEmailVerifyExpiredAt().isBefore(LocalDateTime.now())) {
            return false;
        }

        user.setStatus("ACTIVE");
        user.setEmailVerifyToken(null);
        user.setEmailVerifyExpiredAt(null);
        userRepository.save(user);
        return true;
    }

    @Override
    public List<User> getUsersByRole(String roleName) {
        return userRepository.findUsersByRoleName(roleName);
    }
    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<UserDTO> getInstructors() {
        // Assuming instructors are users with role "MANAGER" or similar
        List<User> instructors = userRepository.findAllByRoleName("Manager");
        return instructors.stream()
                .map(userMapper::toDTO)
                .toList();
    }
}