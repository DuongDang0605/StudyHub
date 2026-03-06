package org.example.studyhub.service.impl;

import jakarta.transaction.Transactional;
import org.example.studyhub.dto.UserDTO;
import org.example.studyhub.mapper.UserMapper;
import org.example.studyhub.model.User;
import org.example.studyhub.reponsitory.SettingRepository;
import org.example.studyhub.reponsitory.UserRepository;
import org.example.studyhub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public List<UserDTO> getAllUsers(){
        List<User> users = userRepository.findAll();
        return userMapper.toDTOList(users);
    }

    @Override
    public Page<UserDTO> searchUsers(Long roleId, String status, String keyword, int page, int size) {
        // Normalize parameters
        Long finalRoleId = (roleId != null && roleId == 0) ? null : roleId;
        String finalStatus = (status != null && "All Statuses".equals(status)) ? null : status;
        String finalKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        String finalRoleName = settingRepository.findRoleByRoleId(finalRoleId).getName();
        // Create pageable
        Pageable pageable = PageRequest.of(page, size);

        Page<User> userPage = userRepository.searchUsers(finalStatus,finalRoleName, finalKeyword,pageable);

        // Convert to DTO
        List<UserDTO> dtoList = userMapper.toDTOList(userPage.getContent());

        return new PageImpl<>(dtoList, pageable, userPage.getTotalElements());
    }
}
