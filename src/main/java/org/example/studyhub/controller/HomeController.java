package org.example.studyhub.controller;

import org.example.studyhub.annotation.RequireRole;
import org.example.studyhub.dto.CourseDTO;
import org.example.studyhub.repository.SettingRepository;
import org.example.studyhub.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

@Controller
@RequestMapping("/home")
public class HomeController {
    @Autowired
    private CourseService courseService;
    @Autowired
    private SettingRepository settingRepository;

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
    @RequireRole("Admin")
    @GetMapping("/settings/toggle/{id}")
    public String toggleSettingStatus(@PathVariable Long id,
                                      @RequestParam(required = false) Long settingTypeId,
                                      @RequestParam(defaultValue = "ALL") String settingStatus,
                                      @RequestParam(defaultValue = "") String settingKeyword,
                                      RedirectAttributes redirectAttributes) {
        settingRepository.findById(id).ifPresent(setting -> {
            String current = setting.getStatus() == null ? "INACTIVE" : setting.getStatus().trim().toUpperCase();
            setting.setStatus("ACTIVE".equals(current) ? "INACTIVE" : "ACTIVE");
            settingRepository.save(setting);
        });

        redirectAttributes.addAttribute("section", "settings");
        if (settingTypeId != null) redirectAttributes.addAttribute("settingTypeId", settingTypeId);
        if (settingStatus != null && !settingStatus.isBlank()) redirectAttributes.addAttribute("settingStatus", settingStatus);
        if (settingKeyword != null && !settingKeyword.isBlank()) redirectAttributes.addAttribute("settingKeyword", settingKeyword);

        return "redirect:/setting";
    }
}
