package com.hrm.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hrm.dto.EmployeeDto;
import com.hrm.mapper.EmployeeMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeMapper employeeMapper;
    
    public EmployeeDto getEmployeeById(Integer employeeId) {
        try {
            return employeeMapper.getEmployeeById(employeeId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get employee with ID: " + employeeId, e);
        }
    }
    
    @Transactional
    public void updateProfile(EmployeeDto employee) {
        try {
            employeeMapper.updateProfile(employee);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update profile", e);
        }
    }
}