package com.hrm.controller;

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

@Controller
@RequestMapping("/salary")
public class SalaryController {

	@Autowired
	SalaryService salaryService;

	// 인사 전용 - 모든 사원 급여 조회
	@GetMapping("/manage")
	public String manageSalaries(Model m) {
		List<SalaryDto> salaries = salaryService.getAllSalaries();
		m.addAttribute("salaries", salaries);
		return "salary/manage";
	}

	// 사원 전용 - 개인 사원 급여 조회
	@GetMapping("/view/{employeeId}")
	public String viewSalary(@PathVariable("employeeId") Integer employeeId, Model m) {
		List<SalaryDto> salary = salaryService.getSalariesByEmployeeId(employeeId);
		m.addAttribute("salary", salary);
		return "salary/view";
	}

	// 인사 전용 - 급여 수정 페이지
	@GetMapping("/edit/{employeeId}")
	public String editSalary(@PathVariable("employeeId") Integer employeeId, Model m) {
		List<SalaryDto> salary = salaryService.getSalariesByEmployeeId(employeeId);
		m.addAttribute("salary", salary);
		return "salary/edit";
	}

	// 인사 전용 - 급여 수정 처리
	@PostMapping("/edit")
	public String updateSalary(@ModelAttribute SalaryDto salaryDto) {
		salaryService.updateSalary(salaryDto);
		return "redirect:/salary/manage";
	}

	// 인사 전용 - 새로운 급여 추가 페이지
	@GetMapping("/add")
	public String addSalaryForm(Model m) {
		m.addAttribute("salary", new SalaryDto());
		return "salary/add";
	}

	// 인사 전용 - 새로운 급여 추가 처리
	@PostMapping("/add")
	public String addSalary(@ModelAttribute SalaryDto salaryDto) {
		salaryService.addSalary(salaryDto);
		return "redirect:/salary/manage";
	}

	// 인사 전용 - 급여 관련 삭제 처리
	@GetMapping("/delete/{employeeId}")
	public String deleteSalary(@PathVariable("employeeId") Integer employeeId) {
		salaryService.deleteSalary(employeeId);
		return "redirect:/salary/manage";
	}

	@GetMapping("/list")
	public String getMonthlySalaryList(@RequestParam(value = "employeeId", required = false) Integer employeeId,
			Model model) {
		if (employeeId == null) {
			employeeId = 1; // 테스트용 기본 ID
		}
		
		Integer employeeId1 = 1001; // 테스트용 사원번호
		
		List<SalaryDto> salaries = salaryService.getSalariesByEmployeeId(employeeId1);

		if (!salaries.isEmpty()) {
			SalaryDto firstSalary = salaries.get(0);
			model.addAttribute("employee", firstSalary.getEmployee());
		}

		model.addAttribute("salaries", salaries);

		return "salary/monthlySalaryList";
	}

	@GetMapping("/detail/{salaryId}")
	public String getSalaryDetail(@PathVariable("salaryId") Integer salaryId, Model model) {
		SalaryDto salary = salaryService.getSalaryById(salaryId);
		model.addAttribute("salary", salary);
		return "salary/salaryDetail";
	}
}
