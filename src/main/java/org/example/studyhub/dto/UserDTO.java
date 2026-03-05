package org.example.studyhub.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private Long id;
    private String fullName;
    private String username;
    private String email;
    private String mobile;
    private String avatar;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}