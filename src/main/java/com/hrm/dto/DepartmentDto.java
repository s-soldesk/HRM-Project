package com.hrm.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DepartmentDto {
	private Integer departmentId; // 부서번호
	private String departmentname; // 부서이름
	private String location; // 부서위치
}
