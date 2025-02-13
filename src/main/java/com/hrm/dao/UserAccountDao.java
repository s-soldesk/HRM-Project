package com.hrm.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import com.hrm.dto.UserAccountDto;

@Mapper
public interface UserAccountDao {
	UserAccountDto findByEmployeeId(@Param("employeeId") String employeeId);

	@Insert({ """
			INSERT INTO UserAccounts(
				EmployeeID,
				Username,
				Password,
				Role
			) VALUES(
				#{employeeId},
				#{username},
				#{password},
				#{role}
			)
			""" })
	@Options(useGeneratedKeys = true, keyProperty = "userId")
	int addUserAccount(UserAccountDto userAccountDto);
}