package org.example.studyhub.controller;

import lombok.RequiredArgsConstructor;
import org.example.studyhub.dto.CourseDTO;
import org.example.studyhub.model.Chapter;
import org.example.studyhub.model.Course;
import org.example.studyhub.service.ChapterService;
import org.example.studyhub.service.CourseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/courses")
@RequiredArgsConstructor
public class ChapterController {

    private final ChapterService chapterService;
    private final CourseService courseService;

    @GetMapping("/{courseId}/content")
    public String getCourseContent(@PathVariable Long courseId, Model model) {

        // 1. Đổi kiểu dữ liệu thành CourseDTO và dùng .orElseThrow()
        CourseDTO course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Course với ID: " + courseId));

        // 2. ĐƯA BIẾN VÀO MODEL
        model.addAttribute("course", course);

        // 3. Lấy danh sách chương học và bài học
        List<Chapter> chapters = chapterService.getChaptersByCourseId(courseId);
        model.addAttribute("chapters", chapters);

        return "admin/course/course-content";
    }

    // =========================================================================
    // CÁC HÀM BÊN DƯỚI LÀ ĐỂ XỬ LÝ FORM THÊM/SỬA/XÓA (Màn hình 4.4)
    // =========================================================================

    // 1. Hiển thị Form Thêm mới Chapter
    @GetMapping("/{courseId}/chapters/new")
    public String showAddChapterForm(@PathVariable Long courseId, Model model) {
        Chapter chapter = new Chapter();
        chapter.setOrderNum(1); // Set thứ tự mặc định

        model.addAttribute("chapter", chapter);
        model.addAttribute("courseId", courseId);
        model.addAttribute("pageTitle", "Add New Chapter");

        // Đã cập nhật đường dẫn tới đúng thư mục admin/chapter/
        return "admin/chapter/chapter-form";
    }

    // 2. Hiển thị Form Sửa Chapter
    @GetMapping("/{courseId}/chapters/{chapterId}/edit")
    public String showEditChapterForm(@PathVariable Long courseId, @PathVariable Long chapterId, Model model) {
        Chapter chapter = chapterService.getChapterById(chapterId);

        model.addAttribute("chapter", chapter);
        model.addAttribute("courseId", courseId);
        model.addAttribute("pageTitle", "Edit Chapter");

        // Đã cập nhật đường dẫn tới đúng thư mục admin/chapter/
        return "admin/chapter/chapter-form";
    }

    // 3. Xử lý Lưu dữ liệu (Dùng chung cho cả Thêm và Sửa)
    @PostMapping("/{courseId}/chapters/save")
    public String saveChapter(@PathVariable Long courseId,
                              @ModelAttribute("chapter") Chapter chapter,
                              RedirectAttributes redirectAttributes) {

        // Gán khóa học cho chapter
        Course course = new Course();
        course.setId(courseId);
        chapter.setCourse(course);

        if (chapter.getId() == null) {
            chapterService.createChapter(chapter);
            redirectAttributes.addFlashAttribute("success", "Chapter added successfully!");
        } else {
            chapterService.updateChapter(chapter.getId(), chapter);
            redirectAttributes.addFlashAttribute("success", "Chapter updated successfully!");
        }

        return "redirect:/admin/courses/" + courseId + "/content";
    }

    // 4. Xử lý Xóa Chapter
    @GetMapping("/{courseId}/chapters/{chapterId}/delete")
    public String deleteChapter(@PathVariable Long courseId, @PathVariable Long chapterId, RedirectAttributes redirectAttributes) {
        chapterService.deleteChapter(chapterId);
        redirectAttributes.addFlashAttribute("success", "Chapter deleted successfully!");
        return "redirect:/admin/courses/" + courseId + "/content";
    }
}