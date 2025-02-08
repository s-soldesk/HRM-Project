package com.hrm.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	public Map<String, Object> getEmployeeList(@RequestParam(name = "page", defaultValue = "1") int page) {
		final int PAGE_SIZE = 5; // 페이지 크기 (상수)
		int offset = (page - 1) * PAGE_SIZE;
		int total = eService.totalEmployees();

		// 데이터 조회
		List<EmployeeDto> employees = eService.employeesList(offset, PAGE_SIZE);
		int totalPages = (total + PAGE_SIZE - 1) / PAGE_SIZE;

		// 현재 페이지가 유효한 범위에 있는지
		if (page > totalPages)
			page = totalPages;

		Map<String, Object> map = new HashMap<>();
		map.put("employees", employees);
		map.put("page", page);
		map.put("pageSize", PAGE_SIZE);
		map.put("totalPages", totalPages);
		return map;
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
	public Map<String, Object> searchEmployee(@RequestParam("searchType") String searchType,
			@RequestParam("keyword") String keyword, @RequestParam(name = "page", defaultValue = "1") int page) {

		final int PAGE_SIZE = 5;
		int offset = (page - 1) * PAGE_SIZE;
		int total = eService.totalSearchEmployees(searchType, keyword);
		System.out.println(total);

		// 데이터 조회
		List<EmployeeDto> searchResults = eService.searchEmployee(searchType, keyword, offset, PAGE_SIZE);
		int totalPages = (total + PAGE_SIZE - 1) / PAGE_SIZE;

		// 현재 페이지가 유효한 범위에 있는지
		if (page > totalPages) {
			page = totalPages;
		}

		Map<String, Object> map = new HashMap<>();
		map.put("employees", searchResults);
		map.put("page", page);
		map.put("pageSize", PAGE_SIZE);
		map.put("totalPages", totalPages);
		map.put("searchType", searchType); // 검색 파라미터 추가
		map.put("keyword", keyword); // 검색 파라미터 추가

		return map;
	}

}
