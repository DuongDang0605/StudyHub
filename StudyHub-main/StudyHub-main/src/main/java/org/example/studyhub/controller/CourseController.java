package org.example.studyhub.controller;

import java.util.List;

import org.example.studyhub.dto.CourseDTO;
import org.example.studyhub.service.CourseService;
import org.example.studyhub.service.SettingService;
import org.example.studyhub.service.UserService;
import org.hibernate.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/courses")
public class CourseController {

    private final CourseService courseService;
    private final UserService userService;
    private final SettingService settingService;

    @GetMapping

    public String listCourses(Model model) {
        List<CourseDTO> courses = courseService.getAllCourses();
        
        // Add dropdown data
        model.addAttribute("categories", settingService.getSettingsByType("CATEGORY"));
        model.addAttribute("levels", settingService.getSettingsByType("LEVEL"));
        model.addAttribute("instructors", userService.getInstructors());
        model.addAttribute("courses", courses);

        return "admin/course/course-list";
    }

    @GetMapping("/new")

    public String createCourseForm(Model model) {
        model.addAttribute("course", new CourseDTO());
        model.addAttribute("categories", settingService.getSettingsByType("CATEGORY"));
        model.addAttribute("levels", settingService.getSettingsByType("LEVEL"));
        model.addAttribute("instructors", userService.getInstructors());
        model.addAttribute("formMode", "create");
        return "admin/course/course-form";
    }

    @PostMapping("/new")

    public String createCourse(@ModelAttribute CourseDTO courseDTO, 
                              RedirectAttributes redirectAttributes) {
        try {
            courseService.createCourse(courseDTO);
            redirectAttributes.addFlashAttribute("success", "Course created successfully!");
            return "redirect:/admin/courses";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating course: " + e.getMessage());
            return "redirect:/admin/courses/new";
        }
    }

    @GetMapping("/{id}")

    public String viewCourseDetail(@PathVariable Long id, Model model) {
        CourseDTO course = courseService.getCourseById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        
        model.addAttribute("course", course);
        model.addAttribute("categories", settingService.getSettingsByType("CATEGORY"));
        model.addAttribute("levels", settingService.getSettingsByType("LEVEL"));
        model.addAttribute("instructors", userService.getInstructors());
        model.addAttribute("formMode", "detail");  // ✅ Thêm formMode
        return "admin/course/course-form";
    }

    @GetMapping("/{id}/edit")

    public String editCourseForm(@PathVariable Long id, Model model) {
        CourseDTO course = courseService.getCourseById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        
        model.addAttribute("course", course);
        model.addAttribute("categories", settingService.getSettingsByType("CATEGORY"));
        model.addAttribute("levels", settingService.getSettingsByType("LEVEL"));
        model.addAttribute("instructors", userService.getInstructors());
        model.addAttribute("formMode", "edit");
        return "admin/course/course-form";
    }

    @PutMapping("/{id}")
    public String updateCourse(@PathVariable Long id, 
                              @ModelAttribute CourseDTO courseDTO,
                              RedirectAttributes redirectAttributes) {
        try {
            courseService.updateCourse(id, courseDTO);
            redirectAttributes.addFlashAttribute("success", "Course updated successfully!");
            return "redirect:/admin/courses";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating course: " + e.getMessage());
            return "redirect:/admin/courses/" + id + "/edit";
        }
    }

    @DeleteMapping("/{id}")
    public String deleteCourse(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            courseService.deleteCourse(id);
            redirectAttributes.addFlashAttribute("success", "Course deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting course: " + e.getMessage());
        }
        return "redirect:/admin/courses";
    }

    // REST API endpoints for Swagger
    @GetMapping("/api/all")
    @ResponseBody
    public ResponseEntity<List<CourseDTO>> getAllCoursesAPI() {
        List<CourseDTO> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<CourseDTO> getCourseByIdAPI(
             @PathVariable Long id) {
        CourseDTO course = courseService.getCourseById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        return ResponseEntity.ok(course);
    }

    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<CourseDTO> createCourseAPI(@RequestBody CourseDTO courseDTO) {
        try {
            CourseDTO createdCourse = courseService.createCourse(courseDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCourse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<CourseDTO> updateCourseAPI(
             @PathVariable Long id,
            @RequestBody CourseDTO courseDTO) {
        try {
            CourseDTO updatedCourse = courseService.updateCourse(id, courseDTO);
            return ResponseEntity.ok(updatedCourse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteCourseAPI(
           @PathVariable Long id) {
        try {
            courseService.deleteCourse(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
