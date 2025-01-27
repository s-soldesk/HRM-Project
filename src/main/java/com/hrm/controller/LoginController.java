package com.hrm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hrm.dto.UserAccountDto;
import com.hrm.service.UserAccountService;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
   
    @Autowired
    private UserAccountService userAccountService;
    
    @GetMapping("/login")
    public String loginPage() {
        return "login/login";
    }
    
    @PostMapping("/login")
    public String login(@RequestParam("employeeId") String employeeId, 
                       @RequestParam("password") String password, 
                       HttpSession session) {
        UserAccountDto user = userAccountService.login(employeeId, password);
        if (user != null) {
            // 세션에 로그인 정보 저장
            session.setAttribute("loggedInUser", user);
            session.setAttribute("loggedInEmail", employeeId); // 이메일(ID) 추가 저장
            return "redirect:/notices";
        }
        return "redirect:/login?error";
    }
}