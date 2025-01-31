package com.hrm.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hrm.dao.EmployeeDao;
import com.hrm.dto.DepartmentDto;
import com.hrm.dto.EmployeeDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService {

	private final EmployeeDao employeeDao;

	// 사원 리스트 (사원번호, 사원이름, 부서이름)
	public List<EmployeeDto> employeesList() {
		return employeeDao.employeesList();
	}

	// 사원 상세정보 (사원번호, 생년월일, 직급 등...)
	public EmployeeDto employeesDetail(int EmployeeID) {
		return employeeDao.employeesDetail(EmployeeID);
	}

	// 부서 리스트
	public List<DepartmentDto> departmentsList() {
		return employeeDao.departmentList();
	}

	// 사원 추가
	public EmployeeDto addEmployee(EmployeeDto employeeDto) {
		// 사원을 추가하고
		int result = employeeDao.addEmployee(employeeDto);

		if (result > 0) {
			// 추가된 사원의 정보를 반환
			return employeeDao.employeesDetail(employeeDto.getEmployeeId());
		}
		return null;
	}

	// 사원 수정
	public int updateEmployee(EmployeeDto employeeDto) {
		return employeeDao.updateEmployee(employeeDto);
	}

	// 사원 검색
	public List<EmployeeDto> searchEmployee(String searchType, String keyword) {
		if (searchType.equals("id")) {
			try {
				return employeeDao.searchById(Integer.valueOf(keyword));
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("사원 ID는 숫자로 검색해야합니다.");
			}
		} else if (searchType.equals("name"))
			return employeeDao.searchByName(keyword);
		else if (searchType.equals("department"))
			return employeeDao.searchByDept(keyword);
		else
			throw new IllegalArgumentException("존재하지 않는 검색 타입: " + searchType); // 예외
	}
}