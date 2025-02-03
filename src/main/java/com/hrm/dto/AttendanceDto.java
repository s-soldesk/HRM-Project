package com.hrm.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;

@Data
public class AttendanceDto {
	
	private Integer attendanceId; // 근태 id
	private Integer employeeId; // 사원 id
	private String employeeName; // 사원 이름
	private LocalDate date; // 근무 날짜
	private LocalTime checkInTime; // 출근 시간
	private LocalTime checkOutTime; // 퇴근 시간
	private BigDecimal hoursWorked; // 근무 시간
	private BigDecimal overtimeHours; // 초과근무시간
	private String attendanceType; // 근태 유형
	private String remarks; // 근태 사유
	
	
}