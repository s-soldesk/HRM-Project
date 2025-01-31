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
	
	// 검색 카테고리에서 선택 별 사원 정보를 얻기 위한
	List<SalaryDto> getSalariesByEmployeeName(String name);
    List<SalaryDto> getSalariesByPosition(String position);
    List<SalaryDto> getSalariesByDepartment(String department);
    
    // 근태 관련 메서드
    void confirmSalaries(String yearMonth);
    boolean isSalaryCalculated(String yearMonth);
    List<SalaryDto> getCalculatedSalaries(String yearMonth);
}
