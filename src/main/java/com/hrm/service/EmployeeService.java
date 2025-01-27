package com.hrm.service;

import org.springframework.stereotype.Service;

import com.hrm.dto.EmployeeDto;
import com.hrm.mapper.EmployeeMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeMapper employeeMapper;
    
    public EmployeeDto getEmployeeById(String employeeId) {  // 메서드명 변경
        return employeeMapper.getEmployeeById(employeeId);
    }
}