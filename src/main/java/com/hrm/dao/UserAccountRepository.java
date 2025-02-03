package com.hrm.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.hrm.dto.UserAccountDto;

@Mapper
public interface UserAccountRepository {
	UserAccountDto findByEmployeeId(@Param("employeeId") String employeeId);
}
