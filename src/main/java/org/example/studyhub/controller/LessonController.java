package org.example.studyhub.controller;

import org.example.studyhub.model.Chapter;
import org.example.studyhub.model.Lesson;
import org.example.studyhub.service.LessonService;
import org.example.studyhub.service.ChapterService; // Giả sử bạn đã có ChapterService
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/courses")
public class LessonController {

    @Autowired
    private LessonService lessonService;

    @Autowired
    private ChapterService chapterService; // Khai báo này sẽ hết lỗi "Cannot resolve"

    @GetMapping("/{courseId}/chapters/{chapterId}/lessons/new")
    public String showAddLessonForm(@PathVariable Long courseId,
                                    @PathVariable Long chapterId,
                                    Model model) {
        Lesson lesson = new Lesson();
        lesson.setOrderNum(1);
        lesson.setDurationMinutes(0);

        Chapter chapter = chapterService.getChapterById(chapterId);

        model.addAttribute("lesson", lesson);
        model.addAttribute("chapter", chapter);
        model.addAttribute("courseId", courseId);
        return "admin/lesson/lesson-form";
    }

    @GetMapping("/{courseId}/lessons/{lessonId}/edit")
    public String showEditLessonForm(@PathVariable Long courseId,
                                     @PathVariable Long lessonId,
                                     Model model) {
        Lesson lesson = lessonService.getLessonById(lessonId);
        model.addAttribute("lesson", lesson);
        model.addAttribute("chapter", lesson.getChapter());
        model.addAttribute("courseId", courseId);
        return "admin/lesson/lesson-form";
    }

    @PostMapping("/{courseId}/chapters/{chapterId}/lessons/save")
    public String saveLesson(@PathVariable Long courseId,
                             @PathVariable Long chapterId,
                             @ModelAttribute("lesson") Lesson lesson,
                             RedirectAttributes redirectAttributes) {

        Chapter chapter = chapterService.getChapterById(chapterId);
        lesson.setChapter(chapter);

        if (lesson.getId() == null) {
            lessonService.createLesson(lesson);
            redirectAttributes.addFlashAttribute("success", "Success!");
        } else {
            lessonService.updateLesson(lesson.getId(), lesson);
            redirectAttributes.addFlashAttribute("success", "Updated!");
        }

        // Sửa lỗi "Cannot resolve MVC view": Chuyển hướng về trang content
        return "redirect:/admin/courses/" + courseId + "/content";
    }
}