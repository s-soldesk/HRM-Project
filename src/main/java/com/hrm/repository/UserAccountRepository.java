package com.hrm.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hrm.dto.UserAccountDto;
import com.hrm.mapper.UserAccountsMapper;

@Repository
public class UserAccountRepository {
   
   @Autowired
   private UserAccountsMapper userAccountMapper;
   
   public UserAccountDto findByEmployeeIdAndPassword(String employeeId, String password) {
       return userAccountMapper.findByEmployeeIdAndPassword(employeeId, password);
   }
}