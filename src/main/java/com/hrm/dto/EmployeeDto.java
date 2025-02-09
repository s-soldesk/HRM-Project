package com.hrm.dto;

import java.time.LocalDate;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmployeeDto {
	private Integer employeeId; // 사원번호
	private String name; // 사원이름
	private LocalDate dateOfBirth; // 생일
	private String gender; // 성별
	private Integer departmentId; // 부서번호
	private String position; // 직급
	private LocalDate hiredate; // 입사일
	private String status; // 재직상태(Active 재직, Inactive 휴직, Retirement 퇴사)
	private String phonenumber; // 핸드폰
	private String email; // 이메일
	
	// ------------------------- 추가
	
	private String departmentName; // 부서 이름
	private DepartmentDto department; // 부서정보 매핑
}