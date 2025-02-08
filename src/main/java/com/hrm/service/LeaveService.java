package com.hrm.service;

import com.hrm.dao.LeaveDao;
import com.hrm.dto.ScheduleDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaveService {

    @Autowired
    private LeaveDao leaveDao;

    // 모든 휴가 일정 조회
    public List<ScheduleDto> getAllLeaves() {
        return leaveDao.getAllLeaves();
    }

    // 특정 사원의 휴가 일정 조회
    public List<ScheduleDto> getLeavesByEmployee(int employeeId) {
        return leaveDao.getLeavesByEmployee(employeeId);
    }

    // 휴가 일정 추가
    public boolean addLeave(ScheduleDto scheduleDto) {
        return leaveDao.insertLeave(scheduleDto) > 0;
    }

    // 휴가 상태 업데이트
    public boolean updateLeaveStatus(int scheduleId, String status) {
        return leaveDao.updateLeaveStatus(scheduleId, status) > 0;
    }

    // 휴가 일정 삭제
    public boolean deleteLeave(int scheduleId) {
        return leaveDao.deleteLeave(scheduleId) > 0;
    }
}
