package com.hrm.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AttendanceDto {
	private Integer attendanceId; // 근태 id
	private Integer employeeId; // 사원 id
	private String employeeName; // 사원 이름
	private LocalDate date; // 근무 날짜
	private LocalTime checkInTime; // 출근 시간
	private LocalTime checkOutTime; // 퇴근 시간

	private Double hoursWorked; // 근무 시간 
	private Double overtimeHours; // 초과근무시간 
	private String attendanceType; // 근태 유형
	private String remarks; // 근태 사유

	// 추가된 필드 유지
	private String status; // 출근 상태
	private String departmentName; // 부서이름 조인 
	private String position; // 직급 조인
	private Integer workingDays; // 근무일수
	private Double totalWorkedHours;  // 총 근무시간 
    private Double totalOvertimeHours; // 총 초과근무시간 
    private String salaryStatus; // 급여 지급 상태 확인 조인

    // 관계 매핑 추가
    private SalaryDto salary; // 급여정보 매핑
	private DepartmentDto department; // 부서정보 매핑
	private EmployeeDto employee; // 사원 정보 매핑
}

