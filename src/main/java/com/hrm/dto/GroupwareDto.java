package com.hrm.dto;

import java.time.LocalDate;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GroupwareDto {

	private Integer taskId; // 업부 고유 id
	private Integer departmentId; // 부서 id
	private String title; // 업무 제목
	private String description; // 업무 내용
	private LocalDate deadline; // 마감일
	private Integer assignedTo; // 담당자

	private DepartmentDto department; // 부서 정보 매핑
	private EmployeeDto assignedEmployee; // 직원 정보와 매핑
}
