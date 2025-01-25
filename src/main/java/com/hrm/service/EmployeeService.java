package com.hrm.service;

import org.springframework.stereotype.Service;

import com.hrm.dto.EmployeeDto;
import com.hrm.mapper.EmployeeMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService {
   private final EmployeeMapper employeeMapper;
   
   public EmployeeDto getEmployeeByEmail(String email) {
       return employeeMapper.getEmployeeByEmail(email);
   }
}