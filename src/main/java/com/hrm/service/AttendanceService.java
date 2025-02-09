package com.hrm.service;

import java.util.List;

import com.hrm.dto.AttendanceDto;

public interface AttendanceService {
    boolean isAttendanceClosed(Integer employeeId, String yearMonth);
    void closeAttendance(Integer employeeId, String yearMonth);
    List<AttendanceDto> getMonthlyAttendance(Integer employeeId, String yearMonth);
    
    List<AttendanceDto> getAllEmployeeAttendance(String yearMonth);
    List<AttendanceDto> getAttendanceByDepartment(String yearMonth, String department);
    boolean confirmAttendance(String yearMonth);
    
    // 특정 사원 근태 조회
    AttendanceDto getAttendanceDetail(Integer employeeId, String date);
    
    boolean updateAttendanceStatus(Integer employeeId, String date, String status);
    
    List<AttendanceDto> getMonthlyAttendanceSummary(String yearMonth);

    List<AttendanceDto> getEmployeeMonthlyAttendance(Integer employeeId, String yearMonth);
    
    void closeAttendanceForEmployee(Integer employeeId, String yearMonth);

}