package org.example.studyhub.controller;


import org.example.studyhub.model.Setting;
import org.example.studyhub.repository.SettingRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/setting")
public class SettingController {
    private final SettingRepository settingRepository;

    public SettingController(SettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }
    @GetMapping
    public String setting(Model model){
        List<Setting> settings =settingRepository.findByTypeIsNotNullOrderByIdAsc();
        model.addAttribute("settings", settings);
        return "admin/setting/setting-list";
    }
}
