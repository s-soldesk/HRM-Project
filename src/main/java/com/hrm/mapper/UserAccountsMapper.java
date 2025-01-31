package com.hrm.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.hrm.dto.UserAccountDto;
import com.hrm.dto.UserAccounts;

@Mapper
public interface UserAccountsMapper {
    UserAccountDto findByEmployeeIdAndPassword(@Param("employeeId") Integer employeeId, 
                                             @Param("password") String password);
    
    UserAccountDto findByEmployeeId(Integer employeeId);
    int updatePassword(@Param("employeeId") Integer employeeId, 
                      @Param("newPassword") String newPassword);
}