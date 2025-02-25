package com.hrm.service;

import com.hrm.dao.AttendanceDao;
import com.hrm.dto.AttendanceDto;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class AttendanceService {

	@Autowired
	private AttendanceDao attendanceDao;
	
	 private static final LocalTime STANDARD_CHECK_IN_TIME = LocalTime.of(9, 0);  // 출근 기준 09:00
	 private static final LocalTime STANDARD_CHECK_OUT_TIME = LocalTime.of(18, 0); // 퇴근 기준 18:00
	 private static final int STANDARD_WORK_HOURS = 8; // 기본 근무 시간
	 private static final int LUNCH_BREAK_MINUTES = 60; // 점심시간 1시간(60분)

	// 근태 기록 조회
	public List<AttendanceDto> searchAttendanceRecords(String employeeId, String name, String startDate, String endDate, String status) {
        return attendanceDao.searchAttendanceRecords(employeeId, name, startDate, endDate, status);
    }
	
	// 특정한 근태 기록과 해당 사원 이름 조회
	public AttendanceDto getAttendanceById(int attendanceId) {
        return attendanceDao.getAttendanceById(attendanceId);
    }

	// 근태 기록 수정
	public boolean updateAttendance(int employeeId, LocalDate date, LocalTime newCheckInTime, LocalTime newCheckOutTime, String status) {
	    // 출근 시간과 퇴근 시간이 없으면 수정 불가능
	    if (newCheckInTime == null || newCheckOutTime == null) {
	        return false;
	    }

	    // 근무 시간 계산
	    double totalMinutesWorked = Math.max(0, ChronoUnit.MINUTES.between(newCheckInTime, newCheckOutTime) - LUNCH_BREAK_MINUTES);
        double hoursWorked = totalMinutesWorked / 60.0;
	    double overtimeHours = Math.max(0, hoursWorked - STANDARD_WORK_HOURS);
	    
	    // 기존 출근 상태 가져오기 (출근 시 Late 여부 확인)
        String checkInStatus = newCheckInTime.isAfter(STANDARD_CHECK_IN_TIME) ? "Late" : "OnTime";
        
        // 퇴근 상태 설정
        String checkOutStatus;
        if (overtimeHours >= 1) {  // 야근이 1시간 이상일 때만 "OverTime"
            checkOutStatus = "OverTime";
        } else if (newCheckOutTime.isBefore(STANDARD_CHECK_OUT_TIME)) {
            checkOutStatus = "LeaveEarly"; // 조퇴
        } else {
            checkOutStatus = "OnTime"; // 정시 퇴근
        }

        // 최종 상태 결정 
        String finalStatus;
        if (checkInStatus.equals("OnTime")) {
        	finalStatus = checkOutStatus;
        }else {
        	if (checkOutStatus.equals("OnTime")) {
        		finalStatus = checkInStatus;
        	}else {
        		finalStatus = checkOutStatus;
        	}
        }

	    
	    // 근태 기록 업데이트
        attendanceDao.updateAttendance(employeeId, date, newCheckInTime, newCheckOutTime, hoursWorked, overtimeHours, finalStatus);
	    return true;
	}
	
	
    
}