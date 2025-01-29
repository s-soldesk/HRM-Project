package com.hrm.dao;

import com.hrm.dto.EmployeeDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

@Mapper
public interface EmployeeDao {
	
	List<EmployeeDto> findAllEmployees();

    // 사원 ID로 사원 존재 여부 확인
    boolean existsById(@Param("employeeId") int employeeId);

    // 사원 ID로 사원 정보 조회
    Optional<EmployeeDto> findById(@Param("employeeId") int employeeId);

    // 이메일로 사원 정보 조회 (로그인 등에 활용 가능)
    Optional<EmployeeDto> findByEmail(@Param("email") String email);
}
