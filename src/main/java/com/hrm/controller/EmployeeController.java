package com.hrm.controller;

import java.security.Principal;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.hrm.dto.EmployeeDto;
import com.hrm.service.EmployeeService;

import jakarta.servlet.http.HttpSession;

import org.springframework.ui.Model;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping("/profile")
    public String getProfile(HttpSession session, Model model) {
        String employeeId = (String) session.getAttribute("loggedInEmail");
        if (employeeId != null) {
            EmployeeDto employee = employeeService.getEmployeeById(employeeId);
            model.addAttribute("employee", employee);
        }
        return "employee/profile";
    }
}