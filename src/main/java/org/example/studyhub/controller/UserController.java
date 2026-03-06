package org.example.studyhub.controller;

import org.example.studyhub.dto.SettingDTO;
import org.example.studyhub.service.SettingService;
import org.example.studyhub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private SettingService settingService;

    @GetMapping("/list")
    public String listUser(Model model,
                           @RequestParam(name = "roleId", required = false) Long roleId,
                           @RequestParam(name = "keyword", required = false) String keyword,
                           @RequestParam(name = "status", required = false) String status,
                           @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size){
        model.addAttribute("page", "user_list");
        var userPage = userService.searchUsers(roleId,status,keyword,page,size);

        model.addAttribute("users", userPage.getContent());
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);

        List<SettingDTO> roles = settingService.getAllRole();
        model.addAttribute("roles", roles);
        model.addAttribute("currentRoleId", roleId);
        model.addAttribute("currentStatus", status);
        model.addAttribute("currentKeyword", keyword);
        return "admin/user/user-list";
    }
}
