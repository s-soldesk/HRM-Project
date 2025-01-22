package com.hrm.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AttendanceDto {
	private Integer attendanceId; // 근태 id
	private Integer employeeId; // 사원 id
	private LocalDate date; // 근무 날짜
	private LocalTime checkInTime; // 출근 시간
	private LocalTime checkOutTime; // 퇴근 시간
	private BigDecimal hoursWorked; // 근무 시간
	private BigDecimal overtimeHours; // 초과근무시간
	private String status; // 출근 상태
}