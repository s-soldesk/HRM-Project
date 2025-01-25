package com.hrm.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hrm.dto.EmployeeDto;
import com.hrm.service.EmployeeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/employee")
public class EmployeeManagementController {

	private final EmployeeService eService;

	/*
	 * 사원 리스트 뷰를 반환하지 않고, 응답의 본문에 JSON 형식의 리스트를 반환
	 */
	@GetMapping("/list")
	public List<EmployeeDto> getEmployeeList() {
		return eService.employeesList();
	}

	// 사원의 세부정보
	@GetMapping("/{employeeId}")
	public EmployeeDto getEmployeeDetail(@PathVariable("employeeId") int employeeId) {
		EmployeeDto employee = eService.employeesDetail(employeeId);
		if (employee != null) { // 사원이 있으면
			return employee;
		} else {
			return null;
		}
	}

}
