package org.example.studyhub.mapper;

import org.example.studyhub.dto.UserDTO;
import org.example.studyhub.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserDTO toDTO(User user) {
        if (user == null) return null;

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setMobile(user.getMobile());
        dto.setAvatar(user.getAvatar());
        dto.setStatus(user.getStatus());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());

        return dto;
    }

    public User toEntity(UserDTO dto) {
        if (dto == null) return null;

        User user = new User();
        user.setFullName(dto.getFullName());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setMobile(dto.getMobile());
        user.setAvatar(dto.getAvatar());
        user.setStatus(dto.getStatus());

        return user;
    }

    public List<UserDTO> toDTOList(List<User> users) {
        if (users == null) return null;
        return users.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public User updateEntity(User existing, UserDTO dto) {
        if (existing == null || dto == null) return existing;

        if (dto.getFullName() != null) existing.setFullName(dto.getFullName());
        if (dto.getUsername() != null) existing.setUsername(dto.getUsername());
        if (dto.getEmail() != null) existing.setEmail(dto.getEmail());
        if (dto.getMobile() != null) existing.setMobile(dto.getMobile());
        if (dto.getAvatar() != null) existing.setAvatar(dto.getAvatar());
        if (dto.getStatus() != null) existing.setStatus(dto.getStatus());

        return existing;
    }
}