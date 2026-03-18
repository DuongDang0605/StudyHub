package org.example.studyhub.controller;

import java.util.List;

import org.example.studyhub.annotation.RequireRole;
import org.example.studyhub.dto.CourseDTO;
import org.example.studyhub.model.Chapter;
import org.example.studyhub.model.Course;
import org.example.studyhub.service.ChapterService;
import org.example.studyhub.service.CourseService;
import org.example.studyhub.service.SettingService;
import org.example.studyhub.service.UserService;
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
@RequireRole({"Admin","Manager"})
public class AdminCourseController {
    private final CourseService courseService;
    private final UserService userService;
    private final SettingService settingService;
    private final ChapterService chapterService;

    @GetMapping

    public String listCourses(Model model) {
        List<CourseDTO> courses = courseService.getAllCourses1();

        // Add dropdown data
        model.addAttribute("categories", settingService.getSettingsByType("CATEGORY"));
        model.addAttribute("levels", settingService.getSettingsByType("LEVEL"));
        model.addAttribute("instructors", userService.getInstructors());
        model.addAttribute("courses", courses);

        return "admin/course/course-list";
    }

    @GetMapping("/new")

    public String createCourseForm(Model model) {
        CourseDTO newCourse = new CourseDTO();
        newCourse.setStatus("UNPUBLISHED");  // Default status
        newCourse.setIsFeatured(false);   // Default featured

        model.addAttribute("course", newCourse);
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
            System.out.println("=== CREATE COURSE DEBUG ===");
            System.out.println("CourseDTO received: " + courseDTO);
            System.out.println("Title: " + courseDTO.getTitle());
            System.out.println("Duration: " + courseDTO.getDuration());
            System.out.println("ListedPrice: " + courseDTO.getListedPrice());

            CourseDTO createdCourse = courseService.createCourse(courseDTO);
            System.out.println("Course created with ID: " + createdCourse.getId());

            redirectAttributes.addFlashAttribute("success", "Course created successfully!");
            return "redirect:/admin/courses";
        } catch (Exception e) {
            System.err.println("Error creating course: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error creating course: " + e.getMessage());
            return "redirect:/admin/courses/new";
        }
    }

    @GetMapping("/{id}/detail")

    public String viewCourseDetail(@PathVariable Long id, Model model) {
        CourseDTO course = courseService.getCourseById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        List<Chapter> chapters = chapterService.getChaptersByCourseId(id);
        model.addAttribute("course", course);
        model.addAttribute("chapters", chapters);
        return "admin/course/course-detail";
    }

//    @GetMapping("/{id}/content")
//    public String courseContent(@PathVariable Long id, Model model) {
//
//        CourseDTO course = courseService.getCourseById(id)
//                .orElseThrow(() -> new RuntimeException("Course not found"));
//
//        List<Chapter> chapters = chapterService.getChaptersByCourse(id);
//
//        model.addAttribute("course", course);
//        model.addAttribute("chapters", chapters);
//
//        return "admin/course/course-content";
//    }

    @GetMapping("/{id}/edit")

    public String editCourseForm(@PathVariable Long id, Model model) {
        CourseDTO course = courseService.getCourseById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        model.addAttribute("course", course);
        model.addAttribute("categories", settingService.getSettingsByType("CATEGORY"));
        model.addAttribute("levels", settingService.getSettingsByType("LEVEL"));
        model.addAttribute("instructors", userService.getInstructors());
        System.out.println("INSTRUCTORS: " + userService.getInstructors());
        model.addAttribute("formMode", "edit");
        return "admin/course/course-form";
    }

    @PostMapping("/{id}")
    public String updateCourse(@PathVariable Long id,
                               @ModelAttribute CourseDTO courseDTO,
                               RedirectAttributes redirectAttributes) {
        try {
            System.out.println("=== UPDATE COURSE DEBUG ===");
            System.out.println("Course ID: " + id);
            System.out.println("CourseDTO received: " + courseDTO);

            courseService.updateCourse(id, courseDTO);
            System.out.println("Course updated successfully");

            redirectAttributes.addFlashAttribute("success", "Course updated successfully!");
            return "redirect:/admin/courses";
        } catch (Exception e) {
            System.err.println("Error updating course: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error updating course: " + e.getMessage());
            return "redirect:/admin/courses/" + id + "/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteCourse(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            courseService.deleteCourse(id);
            redirectAttributes.addFlashAttribute("success", "Course deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting course: " + e.getMessage());
        }
        return "redirect:/admin/courses";
    }

    @GetMapping("/api/all")
    @ResponseBody
    public ResponseEntity<List<CourseDTO>> getAllCoursesAPI() {
        List<CourseDTO> courses = courseService.getAllCourses1();
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
