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

    /**
     * ✅ 모든 직원의 일정 조회
     */
    public List<ScheduleDto> getAllSchedules() {
        return scheduleDao.getAllSchedules();
    }

    /**
     * ✅ 특정 일정 조회 (ID 기준)
     */
    public ScheduleDto getScheduleById(int scheduleId) {
        return scheduleDao.getScheduleById(scheduleId);
    }

    
    /**
     * ✅ 일정 추가
     */
    public void createSchedule(ScheduleDto scheduleDto) {
        scheduleDao.createSchedule(scheduleDto);
    }

    /**
     * ✅ 일정 수정
     */
    public void updateSchedule(ScheduleDto scheduleDto) {
        scheduleDao.updateSchedule(scheduleDto);
    }

    /**
     * ✅ 일정 삭제
     */
    public void deleteSchedule(int scheduleId) {
        scheduleDao.deleteSchedule(scheduleId);
    }
    
    public boolean existsSchedule(int scheduleId) {
        return scheduleDao.countScheduleById(scheduleId) > 0;
    }
}
