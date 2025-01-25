package com.hrm.controller;

import java.security.Principal;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.hrm.dto.EmployeeDto;
import com.hrm.service.EmployeeService;
import org.springframework.ui.Model;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping("/profile")
    public String getProfile(Principal principal, Model model) {
        EmployeeDto employee = employeeService.getEmployeeByEmail(principal.getName());
        model.addAttribute("employee", employee);
        return "employee/profile";
    }
}