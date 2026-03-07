package org.example.studyhub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dash")
public class DashboardController {
    @GetMapping
    public String getDash(){
        return "admin/dashboard/dashboard";
    }

}
