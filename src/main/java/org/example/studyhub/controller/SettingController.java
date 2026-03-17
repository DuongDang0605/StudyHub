package org.example.studyhub.controller;

import org.example.studyhub.dto.SettingDTO;
import org.example.studyhub.mapper.SettingMapper;
import org.example.studyhub.model.Setting;
import org.example.studyhub.repository.SettingRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/setting")
public class SettingController {

    private final SettingRepository settingRepository;
    private final SettingMapper settingMapper;

    public SettingController(SettingRepository settingRepository, SettingMapper settingMapper) {
        this.settingRepository = settingRepository;
        this.settingMapper = settingMapper;
    }

    @GetMapping
    public String setting(Model model) {
        model.addAttribute("settings", settingRepository.findByTypeIsNotNullOrderByIdAsc());
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