package com.hrm.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hrm.dao.ProfileDao;
import com.hrm.dto.EmployeeDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class ProfileService {
    private final ProfileDao profileMapper;
    
    public EmployeeDto getEmployeeById(Integer employeeId) {
        try {
            return profileMapper.getEmployeeById(employeeId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get employee with ID: " + employeeId, e);
        }
    }
    
    @Transactional
    public void updateProfile(EmployeeDto employee) {
        try {
        	profileMapper.updateProfile(employee);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update profile", e);
        }
    }
}