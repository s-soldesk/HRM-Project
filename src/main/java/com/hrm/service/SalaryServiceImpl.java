package com.hrm.service;

import com.hrm.dto.SalaryDto;
import com.hrm.dao.SalaryDao;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalaryServiceImpl implements SalaryService {

    private final SalaryDao salaryMapper;

    public SalaryServiceImpl(SalaryDao salaryMapper) {
        this.salaryMapper = salaryMapper;
    }

    @Override
    public List<SalaryDto> getAllSalaries() {
        return salaryMapper.getAllSalaries();
    }

    @Override
    public SalaryDto getSalaryByEmployeeId(Integer employeeId) {
        return salaryMapper.getSalaryByEmployeeId(employeeId);
    }

    @Override
    public boolean addSalary(SalaryDto salaryDto) {
        return salaryMapper.addSalary(salaryDto) > 0;
    }

    @Override
    public boolean updateSalary(SalaryDto salaryDto) {
        return salaryMapper.updateSalary(salaryDto) > 0;
    }

    @Override
    public boolean deleteSalary(Integer salaryId) {
        return salaryMapper.deleteSalary(salaryId) > 0;
    }

}
