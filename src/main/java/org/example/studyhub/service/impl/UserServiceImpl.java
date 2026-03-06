package org.example.studyhub.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.studyhub.dto.UserDTO;
import org.example.studyhub.mapper.UserMapper;
import org.example.studyhub.model.User;
import org.example.studyhub.model.UserRole;
import org.example.studyhub.repository.SettingRepository;
import org.example.studyhub.repository.UserRepository;
import org.example.studyhub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SettingRepository settingRepository;

    @Override
    public Page<UserDTO> searchUsers(Long roleId, String status, String keyword, int page, int size) {

        String normalizedStatus = (status != null && !status.trim().isEmpty())
                ? status.trim().toUpperCase() : null;

        String normalizedKeyword = (keyword != null && !keyword.trim().isEmpty())
                ? keyword.trim().toLowerCase() : "";

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<User> userPage = userRepository.searchUsers(normalizedStatus, roleId, normalizedKeyword, pageable);
        var s = userPage.getSize();
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
                    .map(ur -> ur.getRole().getValue()) // Lấy Value (Tên Role) của Setting
                    .toList();
            dto.setRoles(currentRoleNames);
        }
        return dto;
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        if (userDTO.getFullName() == null || userDTO.getFullName().trim().length() < 2) {
            throw new IllegalArgumentException("Full name must be at least 2 characters long.");
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

        user.setPassword("123");
        user.setStatus("UNVERIFIED");

        user = userRepository.save(user);
        return userMapper.toDTO(user);
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));

        if (userDTO.getFullName() == null || userDTO.getFullName().trim().length() < 2) {
            throw new IllegalArgumentException("Full name must be at least 2 characters long.");
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
}