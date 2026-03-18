package org.example.studyhub.controller;

import org.example.studyhub.repository.SettingRepository;
import org.example.studyhub.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequestMapping("/courses")

public class CourseController {
    @Autowired
    private CourseService courseService;
    @Autowired private SettingRepository settingRepository;

    @GetMapping
    public String publicCourses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            Model model) {
        model.addAttribute("courses", courseService.findPublicCourses(keyword, categoryId));
        model.addAttribute("categories", settingRepository.findByTypeIdAndStatus(2L, "ACTIVE"));
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        return "/homepage/course/course-list";
    }

    @GetMapping("/{id}")
    public String courseDetail(@PathVariable Long id, Model model) {
        var course = courseService.getCourseDetailForPublic(id);
        if (course == null) {
            return "redirect:/courses";
        }
        model.addAttribute("course", course);
        return "homepage/course/course-detail";
    }
}
