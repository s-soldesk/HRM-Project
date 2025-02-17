package com.hrm.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.hrm.dto.EmployeeDto;

@Mapper
public interface ProfileDao {
    EmployeeDto getEmployeeById(String employeeId);
    void updateProfile(EmployeeDto employee);
    List<EmployeeDto> getAllEmployees();
    EmployeeDto getEmployeeByEmail(String email);
}