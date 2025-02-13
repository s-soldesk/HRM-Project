package com.hrm.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.hrm.dto.UserAccountDto;

@Mapper
public interface UserAccountDao {

	UserAccountDto findByEmployeeId(@Param("employeeId") String employeeId);
	
	// 비밀번호 변경시 employeeId 와 패스워드 확인
    int updatePassword(@Param("employeeId") String employeeId, 
            @Param("newPassword") String newPassword);
}
