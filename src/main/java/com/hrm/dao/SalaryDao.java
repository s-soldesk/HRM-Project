package com.hrm.dao;

import com.hrm.dto.SalaryDto;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SalaryDao {
    List<SalaryDto> getAllSalaries();
    SalaryDto getSalaryByEmployeeId(Integer employeeId);
    int addSalary(SalaryDto salaryDto);
    int updateSalary(SalaryDto salaryDto);
    int deleteSalary(Integer salaryId);
	List<SalaryDto> getSalariesByEmployeeId(Integer employeeId);
	SalaryDto getSalaryById(Integer salaryId);
}
