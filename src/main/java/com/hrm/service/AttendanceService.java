package com.hrm.service;

import com.hrm.dao.AttendanceDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceDao attendanceDao;

    public boolean recordCheckIn(int employeeId) {
        if (attendanceDao.existsTodayRecord(employeeId, LocalDate.now())) {
            return false;
        }
        LocalTime checkInTime = LocalTime.now().truncatedTo(ChronoUnit.SECONDS); // 소수점 제거
        attendanceDao.insertCheckIn(employeeId, LocalDate.now(), checkInTime);
        return true;
    }

    // 퇴근 기록
    public boolean recordCheckOut(int employeeId) {
        if (!attendanceDao.existsTodayRecord(employeeId, LocalDate.now())) {
            return false;
        }

        // 현재 시간을 퇴근 시간으로 설정
        LocalTime checkOutTime = LocalTime.now().truncatedTo(ChronoUnit.SECONDS);

        // 출근 시간 가져오기
        LocalTime checkInTime = attendanceDao.getCheckInTime(employeeId, LocalDate.now());

        // 근무 시간 계산 (단위: 시간)
        long minutesWorked = ChronoUnit.MINUTES.between(checkInTime, checkOutTime);
        double hoursWorked = minutesWorked / 60.0;

        // 법정 근무 시간 (예: 8시간)
        double standardWorkHours = 8.0;
        double overtimeHours = Math.max(0, hoursWorked - standardWorkHours);

        // 근태 상태 설정
        String attendanceType = "Present";
        if (hoursWorked < standardWorkHours) {
            attendanceType = "EarlyLeave";
        } else if (overtimeHours > 0) {
            attendanceType = "Overtime";
        }

        // 데이터베이스 업데이트
        attendanceDao.updateCheckOut(employeeId, LocalDate.now(), checkOutTime, hoursWorked, overtimeHours, attendanceType);
        return true;
    }
}
