package com.hrm.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hrm.dto.EmployeeDto;
import com.hrm.dto.SalaryDto;
import com.hrm.service.SalaryService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/salary")
public class SalaryController {

	@Autowired
	SalaryService salaryService;
	
	// security 구현 안 해서 임시방편
	@GetMapping("/salary")
	public String redirectToSalaryPage(@RequestParam(required = false) Boolean isHR) {
	    if (Boolean.TRUE.equals(isHR)) {
	        return "redirect:/salary/manage";  // 인사부서용 페이지
	    }
	    return "redirect:/salary/employee";  // 일반사원용 페이지
	}
	
	// 인사 전용 - 모든 사원 급여 조회
	@GetMapping("/manage")
	public String manageSalaries(
	        @RequestParam(name = "searchType", required = false) String searchType,
	        @RequestParam(name = "keyword", required = false) String keyword,
	        Model model) {
	    
	    List<SalaryDto> salaries = new ArrayList<>();
	    
	    if (searchType != null && keyword != null && !keyword.trim().isEmpty()) {
	        salaries = salaryService.searchSalaries(searchType, keyword);
	    }
	    
	    model.addAttribute("salaries", salaries);
	    return "salary/manage";
	}
	
	// 사원 전용 - 개인 사원 급여 조회
	@GetMapping("/employee")
	public String viewEmployeeSalary(
	        @RequestParam(name = "employeeId", required = false) Integer employeeId,
	        Model model) {
	    
	    // 임시로 직원 ID 설정 (나중에 로그인 정보에서 가져올 예정)
	    if (employeeId == null) {
	        employeeId = 1001; // 테스트용 ID
	    }
	    
	    List<SalaryDto> salaries = salaryService.getSalariesByEmployeeId(employeeId);
	    if (!salaries.isEmpty()) {
	        model.addAttribute("employee", salaries.get(0).getEmployee());
	    }
	    model.addAttribute("salaries", salaries);
	    
	    return "salary/employee";
	}
	
	// 급여 명세서
	@GetMapping("/detail/{salaryId}")
	public String getSalaryDetail(@PathVariable("salaryId") Integer salaryId, Model model) {
		SalaryDto salary = salaryService.getSalaryById(salaryId);
		model.addAttribute("salary", salary);
		return "salary/salaryDetail";
	}
}
