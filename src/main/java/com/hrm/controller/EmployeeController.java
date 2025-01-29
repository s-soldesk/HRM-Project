package com.hrm.controller;

import com.hrm.dto.EmployeeDto;
import com.hrm.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/employees")
    public String showEmployeeList(Model model) {
        // Employee 데이터를 조회
        List<EmployeeDto> employees = employeeService.getAllEmployees();
        // 모델에 데이터 추가
        model.addAttribute("employees", employees);
        return "employee-list"; // employee-list.html로 반환
    }
}
