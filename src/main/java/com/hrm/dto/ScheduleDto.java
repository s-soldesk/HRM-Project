package com.hrm.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ScheduleDto {

    private Integer scheduleId; // 일정 고유 id
    private Integer employeeId; // 사원 id
    private String title; // 일정 제목
    private String startDate; // 일정 시작 날짜
    private String endDate; // 일정 종료 날짜
    private boolean allDay; // ✅ 종일 여부 추가

    private EmployeeDto employee; // 사원 정보 매핑
}
