package org.example.studyhub.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettingDTO {

    private Long id;
    private String name;
    private Long typeId;
    private String typeName;
    private String value;
    private Integer priority;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}