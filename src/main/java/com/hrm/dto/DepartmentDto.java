package com.hrm.dto;

import lombok.Data;

@Data
public class DepartmentDto {
	private Integer dapartmentId; // 부서번호
	private String departmentname; // 부서이름
	private String location; // 부서위치
}
