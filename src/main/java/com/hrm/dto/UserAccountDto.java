package com.hrm.dto;

import com.hrm.enums.Role;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserAccountDto {

	private Integer userId; // 사용자 id
	private Integer employeeId; // 사원 id
	private String username; // 사용자 이름
	private String password; // 사용자 비밀번호, Spring security로 암호화 예정
	private Role role; // 권한(관리자 or 인사팀)

	private EmployeeDto employee; // 사원 정보 매핑
}