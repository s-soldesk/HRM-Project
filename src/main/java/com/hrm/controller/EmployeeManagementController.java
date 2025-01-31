package com.hrm.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hrm.dto.DepartmentDto;
import com.hrm.dto.EmployeeDto;
import com.hrm.service.EmployeeService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequiredArgsConstructor
@RequestMapping("/employees")
public class EmployeeManagementController {

	private final EmployeeService eService;

	/*
	 * 사원 리스트 뷰를 반환하지 않고, 응답의 본문에 JSON 형식의 리스트를 반환 (CSR)
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

	/*
	 * 부서 목록 반환 사원을 추가할 때 필요함
	 */
	@GetMapping("/department/list")
	public List<DepartmentDto> getDepartmentList() {
		return eService.departmentsList();
	}

	// 사원 추가
	@PostMapping("add")
	public EmployeeDto addEmployee(@RequestBody EmployeeDto employeeDto) {
		return eService.addEmployee(employeeDto);
	}

	// 사원정보 수정
	@PutMapping("{employeeId}")
	public int updateEmployee(@PathVariable("employeeId") int employeeId, @RequestBody EmployeeDto employeeDto) {
		employeeDto.setEmployeeId(employeeId);
		return eService.updateEmployee(employeeDto);
	}

	// 사원 삭제 (필요한가?)

	// 사원 검색
	@GetMapping("/search")
	public List<EmployeeDto> searchEmployee(@RequestParam("searchType") String searchType, @RequestParam("keyword") String keyword) {
		return eService.searchEmployee(searchType, keyword);
	}

}
