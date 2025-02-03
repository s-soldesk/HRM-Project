package com.hrm.dao;

import com.hrm.dto.SalaryDto;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
    boolean isSalaryCalculated(String yearMonth);
    void confirmSalaries(@Param("employeeId") String employeeId, @Param("yearMonth") String yearMonth);
    void confirmSalaries(Map<String, String> of);
    List<SalaryDto> getCalculatedSalaries(String yearMonth);

    // 특정 사원의 특정 월 급여 조회 메서드
    SalaryDto getEmployeeSalaryByMonth(@Param("employeeId") Integer employeeId, @Param("yearMonth") String yearMonth);
    
    // 근태 비교위한 조회
    public boolean isSalaryClosed(Integer employeeId, String yearMonth);
}
