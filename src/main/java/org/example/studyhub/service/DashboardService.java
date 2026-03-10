package org.example.studyhub.service;

import org.example.studyhub.dto.ChapterDTO;
import org.example.studyhub.dto.CourseDTO;
import org.example.studyhub.dto.SettingDTO;
import org.example.studyhub.dto.UserDTO;


import java.util.List;
import java.util.Map;

public interface DashboardService {
    Map<String, Object> getDashboardData(String username, boolean isAdmin, int page);


    CourseDTO getCourseDtoById(Long id);
    List<ChapterDTO> getChaptersByCourseId(Long courseId);

    void updateCourseFromDto(CourseDTO courseDto);

    List<SettingDTO> getAllCategories();
    List<UserDTO> getAllInstructors();


}