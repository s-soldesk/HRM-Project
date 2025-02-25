package com.hrm.service;

import com.hrm.dao.CommuteDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Service
public class CommuteService {

    @Autowired
    private CommuteDao commuteDao;
    
    private static final LocalTime STANDARD_CHECK_IN_TIME = LocalTime.of(9, 0);  // 출근 기준 09:00
    private static final LocalTime STANDARD_CHECK_OUT_TIME = LocalTime.of(18, 0); // 퇴근 기준 18:00
    private static final int STANDARD_WORK_HOURS = 8; // 기본 근무 시간
    private static final int LUNCH_BREAK_MINUTES = 60; // 점심시간 1시간(60분)


    // 출근 기록
    public boolean recordCheckIn(int employeeId) {
        if (commuteDao.existsTodayRecord(employeeId, LocalDate.now())) {
            return false;
        }

        LocalTime checkInTime = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
        String status = checkInTime.isAfter(STANDARD_CHECK_IN_TIME) ? "Late" : "OnTime";

        commuteDao.insertCheckIn(employeeId, LocalDate.now(), checkInTime, status);
        return true;
    }

    // 퇴근 기록
    public boolean recordCheckOut(int employeeId) {
        if (!commuteDao.existsTodayRecord(employeeId, LocalDate.now())) {
            return false;
        }

        LocalTime checkOutTime = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
        LocalTime checkInTime = commuteDao.getCheckInTime(employeeId, LocalDate.now());

        if (checkInTime == null) {
            checkInTime = STANDARD_CHECK_IN_TIME; // 출근 기록이 없으면 기본 출근 시간 설정
        }
        
        // 근무 시간 계산
        double totalMinutesWorked = Math.max(0, ChronoUnit.MINUTES.between(checkInTime, checkOutTime) - LUNCH_BREAK_MINUTES);
        double hoursWorked = totalMinutesWorked / 60.0;
        double overtimeHours = Math.max(0, hoursWorked - STANDARD_WORK_HOURS);
        
        // 기존 출근 상태 가져오기 (출근 시 "Late" 여부 확인)
        String checkInStatus = checkInTime.isAfter(STANDARD_CHECK_IN_TIME) ? "Late" : "OnTime";

        // 퇴근 상태 설정
        String checkOutStatus;
        if (overtimeHours >= 1) {  // 야근이 1시간 이상일 때만 "OverTime"
            checkOutStatus = "OverTime";
        } else if (checkOutTime.isBefore(STANDARD_CHECK_OUT_TIME)) {
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

        commuteDao.updateCheckOut(employeeId, LocalDate.now(), checkOutTime, hoursWorked, overtimeHours, finalStatus);
        return true;
    }
    
    	
    // 이미 퇴근 기록이 있는지 확인
    public boolean hasCheckOutRecord(int employeeId) {
        return commuteDao.hasCheckOutRecord(employeeId, LocalDate.now());
    }
}
