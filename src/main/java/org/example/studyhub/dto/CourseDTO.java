package org.example.studyhub.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDTO {

    private Long id;
    private String title;
    private String description;
    private String thumbnail;
    private Long levelId;
    private String levelName;
    private Long categoryId;
    private String categoryName;
    private Long instructorId;
    private String instructorName;
    private BigDecimal listedPrice;
    private BigDecimal salePrice;
    private Integer duration;
    private String status;
    private Boolean isFeatured;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}