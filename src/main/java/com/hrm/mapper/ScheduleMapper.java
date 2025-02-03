package com.hrm.mapper;

import com.hrm.dto.ScheduleDto;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ScheduleMapper {
    
    // 전체 일정 조회
    List<ScheduleDto> getAllSchedules();

    // 특정 사원의 일정 조회
    List<ScheduleDto> getSchedulesByEmployeeID(@Param("employeeID") int employeeID);

    // 일정 추가
    void createSchedule(ScheduleDto scheduleDto);

    // 일정 수정
    void updateSchedule(ScheduleDto scheduleDto);

    // 일정 삭제
    void deleteSchedule(@Param("scheduleID") int scheduleID);
}
