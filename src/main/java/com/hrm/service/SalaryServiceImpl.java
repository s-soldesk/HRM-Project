package com.hrm.service;

import com.hrm.dto.SalaryDto;
import com.hrm.dao.SalaryDao;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SalaryServiceImpl implements SalaryService {

	private final SalaryDao salaryMapper;

	public SalaryServiceImpl(SalaryDao salaryMapper) {
		this.salaryMapper = salaryMapper;
	}

	@Override
	public List<SalaryDto> getAllSalaries() {
		return salaryMapper.getAllSalaries();
	}

	@Override
	public boolean addSalary(SalaryDto salaryDto) {
		return salaryMapper.addSalary(salaryDto) > 0;
	}

	@Override
	public boolean updateSalary(SalaryDto salaryDto) {
		return salaryMapper.updateSalary(salaryDto) > 0;
	}

	@Override
	public boolean deleteSalary(Integer salaryId) {
		return salaryMapper.deleteSalary(salaryId) > 0;
	}

	@Override
	public List<SalaryDto> getSalariesByEmployeeId(Integer employeeId) {
		return salaryMapper.getSalariesByEmployeeId(employeeId);
	}

	@Override
	public SalaryDto getSalaryById(Integer salaryId) {
		return salaryMapper.getSalaryById(salaryId);
	}

	@Override
	public List<SalaryDto> searchSalaries(String searchType, String keyword) {
		switch (searchType) {
		case "employeeId":
			try {
				int employeeId = Integer.parseInt(keyword);
				return salaryMapper.getSalariesByEmployeeId(employeeId);
			} catch (NumberFormatException e) {
				return new ArrayList<>();
			}
		case "name":
			return salaryMapper.getSalariesByEmployeeName(keyword);
		case "position":
			return salaryMapper.getSalariesByPosition(keyword);
		case "department":
			return salaryMapper.getSalariesByDepartment(keyword);
		default:
			return new ArrayList<>();
		}
	}
}
