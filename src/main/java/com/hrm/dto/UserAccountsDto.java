package com.hrm.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserAccountsDto {
	private Integer userId;
	private Integer employeeId;
	private String username;
	private String password;
	private String role;
	private EmployeeDto employee; // 사원 정보 매핑
}
