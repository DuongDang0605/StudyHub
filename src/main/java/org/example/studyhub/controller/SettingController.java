package org.example.studyhub.controller;

import org.example.studyhub.annotation.RequireRole;
import org.example.studyhub.dto.SettingDTO;
import org.example.studyhub.mapper.SettingMapper;
import org.example.studyhub.model.Setting;
import org.example.studyhub.repository.SettingRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/setting")
@RequireRole("Admin")
public class SettingController {

    private final SettingRepository settingRepository;
    private final SettingMapper settingMapper;

    public SettingController(SettingRepository settingRepository, SettingMapper settingMapper) {
        this.settingRepository = settingRepository;
        this.settingMapper = settingMapper;
    }

    @GetMapping() // Thay đổi đường dẫn này theo ý định routing của bạn (VD: /admin/settings)
    public String getSettings(@RequestParam(required = false) Long settingTypeId,
                              @RequestParam(defaultValue = "ALL") String settingStatus,
                              @RequestParam(defaultValue = "") String settingKeyword,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "5") int size, // Đổi default size thành 5 cho hợp form
                              Model model) {

        // 1. Gắn biến section để Thymeleaf render đúng thẻ th:if="${section == 'settings'}"
        model.addAttribute("section", "settings");

        // 2. Xử lý logic Filter
        String statusFilter = "ALL".equalsIgnoreCase(settingStatus) ? null : settingStatus;
        String keywordFilter = (settingKeyword == null || settingKeyword.isBlank())
                ? null
                : "%" + settingKeyword.trim().toLowerCase() + "%";

        // 3. Đổ dữ liệu các tùy chọn Filter ra View
        model.addAttribute("settingTypes",
                settingRepository.findByTypeIsNullAndStatusOrderByNameAsc("ACTIVE"));
        model.addAttribute("selectedTypeId", settingTypeId);
        model.addAttribute("selectedStatus", settingStatus);
        model.addAttribute("settingKeyword", settingKeyword);

        // 4. Truy vấn dữ liệu phân trang
        var settingPage = settingRepository.searchSettings(
                settingTypeId,
                statusFilter,
                keywordFilter,
                PageRequest.of(page, size, Sort.by("id").ascending())
        );

        // 5. Đổ kết quả danh sách và thông số phân trang ra View
        model.addAttribute("settings", settingPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", settingPage.getTotalPages());
        model.addAttribute("pageSize", size);

        // Trả về file HTML gốc chứa giao diện (nếu file của bạn tên là index.html)
        return "admin/setting/setting-list";
    }

    @GetMapping("/detail")
    public String detail(@RequestParam(required = false) Long id, Model model) {
        SettingDTO form;
        if (id != null) {
            Setting s = settingRepository.findById(id).orElseThrow();
            form = settingMapper.toDTO(s);
        } else {
            form = new SettingDTO();
            form.setPriority(1);
            form.setStatus("ACTIVE");
        }

        model.addAttribute("settingForm", form);
        model.addAttribute("settingTypes", settingRepository.findByTypeIsNullAndStatusOrderByNameAsc("ACTIVE"));
        model.addAttribute("isEdit", id != null);
        return "admin/setting/setting-form";
    }

    @PostMapping("/detail")
    public String save(@ModelAttribute("settingForm") SettingDTO form, Model model) {
        List<String> errors = new ArrayList<>();

        String name = form.getName() == null ? "" : form.getName().trim();
        String value = form.getValue() == null ? null : form.getValue().trim();
        String description = form.getDescription() == null ? null : form.getDescription().trim();

        if (name.isBlank()) errors.add("Name is required.");
        if (name.length() > 20) errors.add("Name max length is 20.");
        if (name.matches(".*\\d.*")) errors.add("Name must not contain digits.");
        if (value != null && value.length() > 100) errors.add("Value max length is 100.");
        if (description != null && description.length() > 200) errors.add("Description max length is 200.");
        if (form.getPriority() == null || form.getPriority() <= 0) errors.add("Priority must be a positive integer.");

        Long typeId = form.getTypeId();
        if (!name.isBlank() && settingRepository.existsDuplicateNameInType(name, typeId, form.getId())) {
            errors.add("Name already exists in selected type.");
        }

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("settingTypes", settingRepository.findByTypeIsNullAndStatusOrderByNameAsc("ACTIVE"));
            model.addAttribute("isEdit", form.getId() != null);
            return "admin/setting/setting-form";
        }

        Setting entity = (form.getId() != null)
                ? settingRepository.findById(form.getId()).orElse(new Setting())
                : new Setting();

        entity.setName(name);
        entity.setValue((value == null || value.isBlank()) ? null : value);
        entity.setDescription((description == null || description.isBlank()) ? null : description);
        entity.setPriority(form.getPriority());
        entity.setStatus("INACTIVE".equalsIgnoreCase(form.getStatus()) ? "INACTIVE" : "ACTIVE");

        if (typeId != null) {
            Setting type = settingRepository.findById(typeId).orElse(null);
            entity.setType(type);
        } else {
            entity.setType(null);
        }

        settingRepository.save(entity);
        return "redirect:/home?section=settings";
    }
}