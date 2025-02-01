package com.hrm.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hrm.dto.UserAccountDto;
import com.hrm.mapper.UserAccountsMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserAccountRepository {
    
    private final UserAccountsMapper userAccountsMapper;
    
    public UserAccountDto findByEmployeeIdAndPassword(String employeeId, String password) {
        try {
            return userAccountsMapper.findByEmployeeIdAndPassword(
                Integer.parseInt(employeeId), 
                password
            );
        } catch (NumberFormatException e) {
            log.error("Invalid employee ID format: {}", employeeId);
            return null;
        }
    }
}