package com.hrm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("errorMsg", "아이디 혹은 비밀번호가 일치하지 않습니다.");
        }
        if (logout != null) {
            model.addAttribute("logoutMsg", "성공적으로 로그아웃되었습니다.");
        }
        return "login";
    }

    @GetMapping("/index")
    public String dashboardPage() {
        return "index";
    }
}

