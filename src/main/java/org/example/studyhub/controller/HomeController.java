package org.example.studyhub.controller;

import org.example.studyhub.dto.CourseDTO;
import org.example.studyhub.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/home")
public class HomeController {
    @Autowired
    private CourseService courseService;

    @GetMapping
    public String getHome(@RequestParam(defaultValue ="") String keyword, Model model) {
        model.addAttribute("keyword",keyword);
        model.addAttribute("publicCourses", courseService.findPublicCourses(keyword, null));
        return "index";
    }

    //@GetMapping("/home/search")
    @ResponseBody
    public List<CourseDTO> search(@RequestParam(defaultValue = "") String keyword) {
        return courseService.findPublicCourses(keyword, null)
                .stream()
                .map(c -> new CourseDTO(c.getId(), c.getTitle(), c.getThumbnail(), c.getDescription()))
                .toList();
    }

}
