package org.example.studyhub.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.studyhub.dto.ChapterDTO;
import org.example.studyhub.dto.CourseDTO;
import org.example.studyhub.dto.SettingDTO;
import org.example.studyhub.dto.UserDTO;
import org.example.studyhub.model.Course;
import org.example.studyhub.model.Setting;
import org.example.studyhub.model.User;
import org.example.studyhub.repository.CourseRepository;
import org.example.studyhub.repository.PostRepository;
import org.example.studyhub.repository.SettingRepository;
import org.example.studyhub.repository.UserRepository;

import org.example.studyhub.service.DashboardService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class DashBoardServiceImpl implements DashboardService {

    private final CourseRepository courseRepository;
    private final SettingRepository settingRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardData() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalCourses", courseRepository.count());
        stats.put("publishedPostsCount", postRepository.countByStatus("PUBLISHED"));
        stats.put("publishedCoursesCount", courseRepository.countByStatus("PUBLISHED"));

        stats.put("recentCourses", courseRepository.findTopRecentCourses(PageRequest.of(0, 7)));
        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public CourseDTO getCourseDtoById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khóa học ID: " + id));

        return CourseDTO.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .categoryId(course.getCategory() != null ? course.getCategory().getId() : null)
                .levelId(course.getLevel() != null ? course.getLevel().getId() : null)
                .instructorId(course.getInstructor() != null ? course.getInstructor().getId() : null)
                .status(course.getStatus())
                .listedPrice(course.getListedPrice())
                .isFeatured(course.getIsFeatured())
                .build();
    }

    @Override
    public List<ChapterDTO> getChaptersByCourseId(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));

        return course.getChapters().stream()
                .map(chapter -> ChapterDTO.builder()
                        .id(chapter.getId())
                        .title(chapter.getTitle())
                        .orderNum(chapter.getOrderNum())
                        .status(chapter.getStatus())
                        .build())
                .sorted(Comparator.comparing(ChapterDTO::getOrderNum))
                .toList();
    }

    @Override
    @Transactional
    public void updateCourseFromDto(CourseDTO dto) {
        Course existing = courseRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khóa học"));

        existing.setTitle(dto.getTitle());
        existing.setDescription(dto.getDescription());
        existing.setStatus(dto.getStatus());
        existing.setListedPrice(dto.getListedPrice());
        existing.setIsFeatured(dto.getIsFeatured());

        if (dto.getCategoryId() != null) {
            existing.setCategory(settingRepository.findById(dto.getCategoryId()).orElse(null));
        }
        if (dto.getLevelId() != null) {
            existing.setLevel(settingRepository.findById(dto.getLevelId()).orElse(null));
        }
        if (dto.getInstructorId() != null) {
            existing.setInstructor(userRepository.findById(dto.getInstructorId()).orElse(null));
        }

        existing.setUpdatedAt(LocalDateTime.now());
        courseRepository.save(existing);
    }

    @Override
    public List<SettingDTO> getAllCategories() {
        return settingRepository.findByTypeIdAndStatus(2L, "ACTIVE").stream()
                .map(s -> {
                    SettingDTO dto = new SettingDTO();
                    BeanUtils.copyProperties(s, dto);
                    return dto;
                })
                .toList();
    }

    @Override
    public List<UserDTO> getAllInstructors() {
        return userRepository.findAll().stream()
                .map(u -> {
                    UserDTO dto = new UserDTO();
                    BeanUtils.copyProperties(u, dto);
                    return dto;
                })
                .toList();
    }
}