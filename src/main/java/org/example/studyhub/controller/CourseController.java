package org.example.studyhub.controller;

import org.example.studyhub.repository.SettingRepository;
import org.example.studyhub.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        return "homepage/course/courses-list";
    }
}
