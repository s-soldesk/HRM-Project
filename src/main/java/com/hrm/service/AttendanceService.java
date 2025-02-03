package com.hrm.service;

import com.hrm.dao.AttendanceDao;
import com.hrm.dto.AttendanceDto;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class AttendanceService {

	@Autowired
	private AttendanceDao attendanceDao;

	// 근태 기록 조회
	public List<AttendanceDto> searchAttendanceRecords(String employeeId, String name, String startDate, String endDate,
			String attendanceType) {
		return attendanceDao.searchAttendanceRecords(employeeId, name, startDate, endDate, attendanceType);
	}
	
	// 특정한 근태 기록과 해당 사원 이름 조회
	public AttendanceDto getAttendanceById(int attendanceId) {
        return attendanceDao.getAttendanceById(attendanceId);
    }

    // 근태 기록 수정
    public void updateAttendance(AttendanceDto attendance) {
        attendanceDao.updateAttendance(attendance);
    }
    
    
    
    
}
