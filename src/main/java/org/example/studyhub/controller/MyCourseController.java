package org.example.studyhub.controller;

import jakarta.servlet.http.HttpSession;
import org.example.studyhub.model.Chapter;
import org.example.studyhub.model.Course;
import org.example.studyhub.model.Enrollment;
import org.example.studyhub.model.Lesson;
import org.example.studyhub.model.User;
import org.example.studyhub.repository.CourseRepository;
import org.example.studyhub.repository.EnrollmentRepository;
import org.example.studyhub.service.ChapterService;
import org.example.studyhub.service.EnrollmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Objects;

@Controller
public class MyCourseController {

    private final EnrollmentService enrollmentService;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ChapterService chapterService;

    public MyCourseController(EnrollmentService enrollmentService,
                              CourseRepository courseRepository,
                              EnrollmentRepository enrollmentRepository,
                              ChapterService chapterService) {
        this.enrollmentService = enrollmentService;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.chapterService = chapterService;
    }

    @GetMapping("/my-courses")
    public String myCourses(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "6") int size,
                            @RequestParam(required = false) String keyword,
                            HttpSession session,
                            Model model) {

        User currentUser = (User) session.getAttribute("loggedInUser");
        if (currentUser == null) return "redirect:/auth/login";

        @SuppressWarnings("unchecked")
        List<String> roleNames = (List<String>) session.getAttribute("roleNames");

        boolean isAdmin = roleNames != null && roleNames.stream().anyMatch(r -> "Admin".equalsIgnoreCase(r));
        boolean isManager = roleNames != null && roleNames.stream().anyMatch(r -> "Manager".equalsIgnoreCase(r));

        String keywordPattern = (keyword == null || keyword.isBlank())
                ? ""
                : "%" + keyword.trim().toLowerCase() + "%";

        Page<?> pageData;
        String viewMode;
        String pageTitle;

        if (isAdmin) {
            pageData = courseRepository.findAllForMyCourses(
                    keywordPattern, PageRequest.of(page, size, Sort.by("createdAt").descending()));
            viewMode = "course";
            pageTitle = "All Courses (Admin)";
        } else if (isManager) {
            pageData = courseRepository.findManagedForMyCourses(
                    currentUser.getId(), keywordPattern, PageRequest.of(page, size, Sort.by("createdAt").descending()));
            viewMode = "course";
            pageTitle = "My Managed Courses";
        } else {
            Page<Enrollment> p = enrollmentService.getMyApprovedCourses(currentUser.getId(), keyword, page, size);
            pageData = p;
            viewMode = "enrollment";
            pageTitle = "My Approved Courses";
        }

        model.addAttribute("myCourses", pageData.getContent());
        model.addAttribute("currentPage", pageData.getNumber());
        model.addAttribute("totalPages", pageData.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        model.addAttribute("viewMode", viewMode);
        model.addAttribute("pageTitle", pageTitle);

        return "homepage/course/my-courses";
    }

    @GetMapping("/my-courses/{courseId}/lesson-view")
    public String lessonView(@PathVariable Long courseId,
                             @RequestParam(required = false) Long lessonId,
                             HttpSession session,
                             Model model) {

        User currentUser = (User) session.getAttribute("loggedInUser");
        if (currentUser == null) return "redirect:/auth/login";

        @SuppressWarnings("unchecked")
        List<String> roleNames = (List<String>) session.getAttribute("roleNames");

        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) {
            return "redirect:/my-courses";
        }

        if (!hasCourseAccess(currentUser, roleNames, course)) {
            return "redirect:/my-courses";
        }

        List<Chapter> chapters = chapterService.getChaptersByCourseId(courseId);
        Lesson selectedLesson = pickLesson(chapters, lessonId);

        model.addAttribute("course", course);
        model.addAttribute("chapters", chapters);
        model.addAttribute("selectedLesson", selectedLesson);

        return "homepage/course/lesson-view";
    }

    private boolean hasCourseAccess(User currentUser, List<String> roleNames, Course course) {
        boolean isAdmin = roleNames != null && roleNames.stream().anyMatch(r -> "Admin".equalsIgnoreCase(r));
        if (isAdmin) return true;

        boolean isManager = roleNames != null && roleNames.stream().anyMatch(r -> "Manager".equalsIgnoreCase(r));
        if (isManager) {
            Long userId = currentUser.getId();
            boolean isCreator = course.getCreatedBy() != null && Objects.equals(course.getCreatedBy().getId(), userId);
            boolean isInstructor = course.getInstructor() != null && Objects.equals(course.getInstructor().getId(), userId);
            return isCreator || isInstructor;
        }

        return enrollmentRepository.hasApprovedEnrollmentAccess(currentUser.getId(), currentUser.getEmail(), course.getId());
    }

    private Lesson pickLesson(List<Chapter> chapters, Long lessonId) {
        if (chapters == null || chapters.isEmpty()) {
            return null;
        }

        if (lessonId != null) {
            for (Chapter chapter : chapters) {
                if (chapter.getLessons() == null) continue;
                for (Lesson lesson : chapter.getLessons()) {
                    if (Objects.equals(lesson.getId(), lessonId)) {
                        return lesson;
                    }
                }
            }
        }

        for (Chapter chapter : chapters) {
            if (chapter.getLessons() != null && !chapter.getLessons().isEmpty()) {
                return chapter.getLessons().get(0);
            }
        }
        return null;
    }
}
