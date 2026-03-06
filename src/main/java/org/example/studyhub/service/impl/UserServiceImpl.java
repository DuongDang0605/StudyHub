package org.example.studyhub.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.studyhub.dto.UserDTO;
import org.example.studyhub.mapper.UserMapper;
import org.example.studyhub.model.User;
import org.example.studyhub.repository.UserRepository;
import org.example.studyhub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;

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
}