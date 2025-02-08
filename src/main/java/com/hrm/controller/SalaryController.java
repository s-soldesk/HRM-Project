package com.hrm.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import com.hrm.dto.SalaryDto;
import com.hrm.service.SalaryService;

@Controller
@RequestMapping("/salary")
public class SalaryController {

	@Autowired
	private SalaryService salaryService;

	// 메인 급여 페이지 - 권한에 따른 리다이렉션
	@GetMapping
	public String salaryMain() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		boolean isAdminOrHR = auth.getAuthorities().stream()
				.anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN") || r.getAuthority().equals("ROLE_HR"));

		return isAdminOrHR ? "redirect:/salary/manage" : "redirect:/salary/employee";
	}

	// 인사팀 전용 - 전체 사원 급여 조회
	@GetMapping("/manage")
	@PreAuthorize("hasAnyRole('HR', 'ADMIN')")
	public String manageSalaries(@RequestParam(name = "searchType", required = false) String searchType,
			@RequestParam(name = "keyword", required = false) String keyword, Model model) {

		List<SalaryDto> salaries;
		if (searchType != null && keyword != null && !keyword.trim().isEmpty()) {
			salaries = salaryService.searchSalaries(searchType, keyword);
		} else {
			salaries = salaryService.getAllSalaries();
		}

		model.addAttribute("salaries", salaries);
		return "salary/manage";
	}

	// 일반 사원 - 개인 급여 조회
	@GetMapping("/employee")
	@PreAuthorize("hasRole('EMPLOYEE')")
	public String viewEmployeeSalary(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		// admin이나 임시 계정이 아닌 경우에만 처리
		if (auth != null && !auth.getName().equals("admin")) {
			try {
				// 현재 로그인한 사용자의 employeeId 가져오기
				Integer employeeId = Integer.parseInt(auth.getName());

				// 해당 사원의 급여 정보 조회
				List<SalaryDto> salaries = salaryService.getSalariesByEmployeeId(employeeId);

				if (!salaries.isEmpty()) {
					// 사원 정보 설정
					model.addAttribute("employee", salaries.get(0).getEmployee());
					model.addAttribute("salaries", salaries);
				} else {
					// 급여 정보가 없는 경우
					model.addAttribute("salaries", new ArrayList<>());
				}

				return "salary/employee";
			} catch (NumberFormatException e) {
				// employeeId가 숫자가 아닌 경우의 처리
				return "redirect:/";
			}
		}

		// admin이나 다른 예외적인 경우의 처리
		return "redirect:/";
	}

	// 급여 명세서 상세 조회
	@GetMapping("/detail/{salaryId}")
	public String getSalaryDetail(@PathVariable("salaryId") Integer salaryId, Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String currentUsername = auth.getName();

		SalaryDto salary = salaryService.getSalaryById(salaryId);

		// admin이나 HR인 경우 모든 급여 정보 조회 가능
		if (hasHRorAdminRole(auth)) {
			model.addAttribute("salary", salary);
			return "salary/salaryDetail";
		}

		// 일반 사원의 경우 자신의 급여 정보만 조회 가능
		try {
			Integer employeeId = Integer.parseInt(currentUsername);
			if (!salary.getEmployeeId().equals(employeeId)) {
				return "redirect:/salary/employee";
			}
		} catch (NumberFormatException e) {
			// admin 계정등 employeeId가 숫자가 아닌 경우
			return "redirect:/salary/employee";
		}

		model.addAttribute("salary", salary);
		return "salary/salaryDetail";
	}

	private boolean hasHRorAdminRole(Authentication auth) {
		return auth.getAuthorities().stream()
				.anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN") || r.getAuthority().equals("ROLE_HR"));
	}
}