package org.example.studyhub.service;

import org.example.studyhub.dto.ChapterDTO;
import org.example.studyhub.dto.CourseDTO;
import org.example.studyhub.dto.SettingDTO;
import org.example.studyhub.dto.UserDTO;
import org.example.studyhub.model.Setting;
import org.example.studyhub.model.User;

import java.util.List;
import java.util.Map;

public interface DashboardService {
    Map<String, Object> getDashboardData();


    CourseDTO getCourseDtoById(Long id);
    List<ChapterDTO> getChaptersByCourseId(Long courseId);

    void updateCourseFromDto(CourseDTO courseDto);

    List<SettingDTO> getAllCategories();
    List<UserDTO> getAllInstructors();
}