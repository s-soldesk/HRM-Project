package com.hrm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/employees")
public class EmployeeViewController {

	/*
	 * GET /employee
	 * employee/index.html 페이지만 렌더링
	 */
	@GetMapping("")
	public String getEmployeePage() {
		return "employee/index";
	}
}
