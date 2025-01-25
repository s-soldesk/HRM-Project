package com.hrm.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hrm.dao.EmployeeDao;
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
}