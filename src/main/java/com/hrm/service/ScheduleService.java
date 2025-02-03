package com.hrm.service;

import com.hrm.dao.ScheduleDao;
import com.hrm.dto.ScheduleDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleDao scheduleDao;

    // 모든 일정 조회
    public List<ScheduleDto> getAllSchedules() {
        return scheduleDao.getAllSchedules();
    }

    // 특정 사원의 일정 조회
    public List<ScheduleDto> getSchedulesByEmployee(int employeeId) {
        return scheduleDao.getSchedulesByEmployee(employeeId);
    }

    // 일정 추가 (근무 일정 또는 휴가 신청)
    public boolean addSchedule(ScheduleDto scheduleDto) {
        return scheduleDao.insertSchedule(scheduleDto) > 0;
    }


    // 일정 상태 업데이트 (HR이 승인 또는 거절)
    public boolean updateScheduleStatus(int scheduleId, String status) {
        return scheduleDao.updateScheduleStatus(scheduleId, status) > 0;
    }

    // 일정 삭제
    public boolean deleteSchedule(int scheduleId) {
        return scheduleDao.deleteSchedule(scheduleId) > 0;
    }
}
