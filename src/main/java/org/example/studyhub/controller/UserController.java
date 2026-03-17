package org.example.studyhub.controller;

import org.example.studyhub.annotation.RequireRole;
import org.example.studyhub.dto.SettingDTO;
import org.example.studyhub.dto.UserDTO;
import org.example.studyhub.service.SettingService;
import org.example.studyhub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity; // Nhớ import thư viện này
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/users")
@RequireRole("Admin")
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

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "admin/user/user-form";
    }

    @PostMapping("/create")
    public String createUser(@ModelAttribute("userDTO") UserDTO userDTO, RedirectAttributes redirectAttributes, Model model) {
        try {
            userService.createUser(userDTO);
            redirectAttributes.addFlashAttribute("successMessage", "User account created successfully!");
            return "redirect:/users/list";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("userDTO", userDTO);
            return "admin/user/user-form";
        }
    }

    @GetMapping("/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        UserDTO userDTO = userService.getUserById(id);
        model.addAttribute("userDTO", userDTO);
        model.addAttribute("allRoles", settingService.getAllRole());
        return "admin/user/user-detail";
    }

    @PostMapping("/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute("userDTO") UserDTO userDTO, RedirectAttributes redirectAttributes, Model model) {
        try {
            userService.updateUser(id, userDTO);
            redirectAttributes.addFlashAttribute("successMessage", "User details updated successfully!");
            return "redirect:/users/list";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("userDTO", userDTO);
            model.addAttribute("allRoles", settingService.getAllRole());
            return "admin/user/user-detail";
        }
    }

    @PostMapping("/{id}/status")
    @ResponseBody
    public ResponseEntity<String> changeUserStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            userService.changeStatus(id, status);
            return ResponseEntity.ok("Success");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}