package com.hrm.mapper;

import com.hrm.dto.ScheduleDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ScheduleMapper {

    /**
     * ✅ 모든 일정 조회
     */
    List<ScheduleDto> getAllSchedules();

    /**
     * ✅ 일정 추가
     */
    void createSchedule(ScheduleDto scheduleDto);

    /**
     * ✅ 일정 수정
     */
    void updateSchedule(ScheduleDto scheduleDto);

    /**
     * ✅ 일정 삭제
     */
    void deleteSchedule(@Param("scheduleId") int scheduleId);
}
