package com.hrm.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ScheduleDto {
    private Integer scheduleId; // 일정 고유 ID
    private Integer employeeId; // 사원 ID
    private String title; // 일정 제목
    private String type; // "Work" 또는 "Leave"
    private String leaveType; // 휴가 유형 ("Annual", "Sick", "Personal", "Unpaid")
    private String reason; // 휴가 사유 (Leave의 경우 필요)
    private String startDate; // 일정 시작 날짜
    private String endDate; // 일정 종료 날짜
    private boolean allDay; // ✅ 종일 여부 추가
    private String status; // 일정 상태 ("CONFIRMED", "PENDING", "REJECTED")
    private String requestDate; // 일정 요청일

    private String employeeName; // 사원 이름
    private EmployeeDto employee; // 사원 정보 매핑
}
