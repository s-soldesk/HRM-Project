package com.hrm.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.hrm.dto.EmployeeDto;

@Mapper
public interface EmployeeMapper {
    EmployeeDto getEmployeeById(String employeeId);  // 메서드명 변경
}