package com.hrm.dto;

import java.time.LocalDate;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmployeeDto {
	private Integer employeeId; // 사원번호
	private String name; // 사원이름
	private LocalDate dateBirth; // 생일
	private String gender; // 성별
	private Integer departmentId; // 부서번호
	private String position; // 직급
	private LocalDate hiredate; // 입사일
	private String status; // 재직상태
	private Integer phonenumber; // 핸드폰
	private String email; // 이메일
	private DepartmentDto department; // 부서정보 매핑
}
