package com.hrm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hrm.service.SalaryService;

@Controller
@RequestMapping("/salary/calculate")
public class SalaryCalculateController {
	
	@Autowired
	private SalaryService salaryService;
	
}
