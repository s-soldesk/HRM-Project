package com.hrm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hrm.dto.UserAccountDto;
import com.hrm.service.UserAccountService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class LoginController {
   
    @Autowired
    private UserAccountService userAccountService;
    
    @GetMapping("/login")
    public String loginPage() {
        return "login/login";
    }
    
    @PostMapping("/login")
    public String login(
            @RequestParam("employeeId") Integer employeeId,
            @RequestParam("password") String password,
            HttpSession session) {
        log.info("Login attempt for employeeId: {}", employeeId);
        
        UserAccountDto user = userAccountService.login(employeeId, password);
        
        if (user != null) {
            log.info("Login successful. User role: {}", user.getRole());
            session.setAttribute("loggedInUser", user);
            session.setAttribute("loggedInEmail", employeeId);
            session.setAttribute("userRole", user.getRole());
            return "redirect:/notices";
        }
        
        return "redirect:/login?error";
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addAttribute("logout", "true");
        return "redirect:/login";
    }
}