package org.example.studyhub.service;

import org.example.studyhub.dto.UserDTO;
import org.example.studyhub.model.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
    Page<UserDTO> searchUsers(Long roleId, String status, String keyword, int page, int size);

    UserDTO getUserById(Long id);
    UserDTO createUser(UserDTO userDTO);
    UserDTO updateUser(Long id, UserDTO userDTO);
    void changeStatus(Long id, String status);

}
