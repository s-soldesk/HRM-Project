package com.hrm.service;

import com.hrm.dto.SalaryDto;
import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface SalaryService {
    // 모든 사원의 급여 조회 (인사 전용)
    List<SalaryDto> getAllSalaries();

    // 특정 사원의 급여 조회 (일반 직원용) (employeeId로 검색)
    List<SalaryDto> getSalariesByEmployeeId(Integer employeeId);
    
    // 특성 사원 검색 (인사 전용)
    List<SalaryDto> searchSalaries(String searchType, String keyword);
    
    SalaryDto getSalaryById(Integer salaryId);
    
    // 급여 데이터 추가 (인사 전용)
    boolean addSalary(SalaryDto salaryDto);
    
    // 급여 데이터 수정 (인사 전용)
    boolean updateSalary(SalaryDto salaryDto);

    // 급여 데이터 삭제 (인사 전용)
    boolean deleteSalary(Integer salaryId);
    
    // 급여 관리 데이터
    void calculateSalaries(Integer employeeId, String yearMonth);
    
    // 급여 계산
    boolean isSalaryCalculated(String yearMonth);
    
    List<SalaryDto> getCalculatedSalaries(String yearMonth);
    
    // 특정 사원의 특정 월 급여 조회 메서드
    SalaryDto getEmployeeSalaryByMonth(Integer employeeId, String yearMonth);
    
    // 근태 확정
	void confirmSalaries(String employeeId, String yearMonth);
	
	// 근태 확인
	public boolean isSalaryClosed(Integer employeeId, String yearMonth);

}
