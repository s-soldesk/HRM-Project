package com.hrm.service;

import com.hrm.dao.EmployeeDao;
import com.hrm.dto.EmployeeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeDao employeeDao;

    public List<EmployeeDto> getAllEmployees() {
        return employeeDao.findAllEmployees();
    }
}
