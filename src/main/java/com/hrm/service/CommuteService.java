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
    private CommuteDao attendanceDao;
    
    private static final LocalTime STANDARD_CHECK_IN_TIME = LocalTime.of(9, 0);  // 출근 기준 09:00
    private static final LocalTime STANDARD_CHECK_OUT_TIME = LocalTime.of(18, 0); // 퇴근 기준 18:00
    private static final int STANDARD_WORK_HOURS = 8; // 기본 근무 시간


    // 출근 기록
    public boolean recordCheckIn(int employeeId) {
        if (attendanceDao.existsTodayRecord(employeeId, LocalDate.now())) {
            return false;
        }

        LocalTime checkInTime = LocalTime.now().truncatedTo(ChronoUnit.SECONDS);
        String attendanceType = checkInTime.isAfter(STANDARD_CHECK_IN_TIME) ? "Late" : "Present";

        attendanceDao.insertCheckIn(employeeId, LocalDate.now(), checkInTime, attendanceType);
        return true;
    }

    // 퇴근 기록
    public boolean recordCheckOut(int employeeId) {
        if (!attendanceDao.existsTodayRecord(employeeId, LocalDate.now())) {
            return false;
        }

        LocalTime checkOutTime = LocalTime.now().truncatedTo(ChronoUnit.SECONDS);
        LocalTime checkInTime = STANDARD_CHECK_IN_TIME; // 기본 출근 시간 (DB 조회 필요 시 쿼리 추가 가능)

        // 근무 시간 계산
        double hoursWorked = ChronoUnit.MINUTES.between(checkInTime, checkOutTime) / 60.0;
        double overtimeHours = Math.max(0, hoursWorked - STANDARD_WORK_HOURS);
        String attendanceType;

        // 근태 유형 설정
        if (checkOutTime.isBefore(STANDARD_CHECK_OUT_TIME)) {
            attendanceType = "EarlyLeave";
        } else if (overtimeHours > 0) {
            attendanceType = "Overtime";
        } else {
            attendanceType = "Present";
        }

        attendanceDao.updateCheckOut(employeeId, LocalDate.now(), checkOutTime, hoursWorked, overtimeHours, attendanceType);
        return true;
    }
    	
    // 이미 퇴근 기록이 있는지 확인
    public boolean hasCheckOutRecord(int employeeId) {
        return attendanceDao.hasCheckOutRecord(employeeId, LocalDate.now());
    }
}
