package com.hrm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hrm.dto.UserAccountDto;
import com.hrm.repository.UserAccountRepository;

@Service
public class UserAccountService {
   
   @Autowired
   private UserAccountRepository userAccountRepository;
   
   public UserAccountDto login(String employeeId, String password) {
       return userAccountRepository.findByEmployeeIdAndPassword(employeeId, password);
   }
}