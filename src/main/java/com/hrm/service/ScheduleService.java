package com.hrm.service;

import com.hrm.mapper.ScheduleMapper;
import com.hrm.dto.ScheduleDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleMapper scheduleMapper;

    /**
     * ✅ 모든 직원의 일정 조회
     */
    public List<ScheduleDto> getAllSchedules() {
        return scheduleMapper.getAllSchedules();
    }

    /**
     * ✅ 일정 추가
     */
    public void createSchedule(ScheduleDto scheduleDto) {
        scheduleMapper.createSchedule(scheduleDto);
    }

    /**
     * ✅ 일정 수정
     */
    public void updateSchedule(ScheduleDto scheduleDto) {
        scheduleMapper.updateSchedule(scheduleDto);
    }

    /**
     * ✅ 일정 삭제
     */
    public void deleteSchedule(int scheduleId) {
        scheduleMapper.deleteSchedule(scheduleId);
    }
}
