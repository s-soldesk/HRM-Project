package com.hrm.service;

import com.hrm.dto.SalaryDto;
import java.util.List;

public interface SalaryService {
    // 모든 사원의 급여 조회 (인사 전용)
    List<SalaryDto> getAllSalaries();

    // 특정 사원의 급여 조회 (일반 직원용) (employeeId로 검색)
    SalaryDto getSalaryByEmployeeId(Integer employeeId);
    
    List<SalaryDto> getSalariesByEmployeeId(Integer employeeId);
    
    SalaryDto getSalaryById(Integer salaryId);
    
    // 급여 데이터 추가 (인사 전용)
    boolean addSalary(SalaryDto salaryDto);

    // 급여 데이터 수정 (인사 전용)
    boolean updateSalary(SalaryDto salaryDto);

    // 급여 데이터 삭제 (인사 전용)
    boolean deleteSalary(Integer salaryId);
}
