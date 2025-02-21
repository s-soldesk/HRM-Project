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

import com.hrm.dao.UserAccountDao;
import com.hrm.dto.EmployeeDto;
import com.hrm.dto.SalaryDto;
import com.hrm.dto.UserAccountDto;
import com.hrm.service.EmployeeService;
import com.hrm.service.SalaryService;

@Controller
@RequestMapping("/salary")
public class SalaryController {

	@Autowired
	private SalaryService salaryService;

	@Autowired
	private UserAccountDao userAccountDao;

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

		if (auth != null && !auth.getName().equals("admin")) {
			String email = auth.getName(); // 로그인한 사용자의 이메일

			try {
				// 이메일로 UserAccount 조회
				UserAccountDto userAccount = userAccountDao.findByEmployeeId(email);

				if (userAccount == null) {
					System.out.println("UserAccount를 찾을 수 없음!");
					return "redirect:/";
				}

				// Employee 테이블에서 이메일로 EmployeeID(정수형) 조회
				Integer employeeId = salaryService.getEmployeeIdByEmail(email);

				if (employeeId == null) {
					System.out.println("EmployeeID를 찾을 수 없음!");
					model.addAttribute("errorMessage", "직원 정보를 찾을 수 없습니다.");
					return "salary/employee";
				}

				// EmployeeID로 급여 조회
				List<SalaryDto> salaries = salaryService.getSalariesByEmployeeId(employeeId);

				// employee가 null인지 확인
				EmployeeDto employee = null;
				if (!salaries.isEmpty() && salaries.get(0).getEmployee() != null) {
					employee = salaries.get(0).getEmployee();
				}

				if (employee == null) {
					System.out.println("Employee 정보가 존재하지 않음!");
					model.addAttribute("errorMessage", "Employee 정보가 존재하지 않습니다!");
					return "salary/employee";
				}

				model.addAttribute("employee", employee);
				model.addAttribute("salaries", salaries);

				return "salary/employee";

			} catch (Exception e) {
				e.printStackTrace();
				model.addAttribute("errorMessage", "시스템 오류가 발생했습니다. 관리자에게 문의하세요.");
				return "salary/employee";
			}
		}

		model.addAttribute("errorMessage", "접근 권한이 없습니다.");
		return "salary/employee";
	}

	// 급여 명세서 상세 조회
	@GetMapping("/detail/{salaryId}")
	public String getSalaryDetail(@PathVariable("salaryId") Integer salaryId, Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String currentEmail = auth.getName();

		SalaryDto salary = salaryService.getSalaryById(salaryId);

		// admin이나 HR인 경우 모든 급여 정보 조회 가능
		if (hasHRorAdminRole(auth)) {
			model.addAttribute("salary", salary);
			return "salary/salaryDetail";
		}

		// 일반 사원의 경우 자신의 급여 정보만 조회 가능
		try {
			// 이메일로 Employee ID 조회
			Integer employeeId = salaryService.getEmployeeIdByEmail(currentEmail);
			if (employeeId == null || !salary.getEmployeeId().equals(employeeId)) {
				return "redirect:/salary/employee";
			}

			model.addAttribute("salary", salary);
			return "salary/salaryDetail";
		} catch (Exception e) {
			return "redirect:/salary/employee";
		}
	}

	private boolean hasHRorAdminRole(Authentication auth) {
		return auth.getAuthorities().stream()
				.anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN") || r.getAuthority().equals("ROLE_HR"));
	}
}