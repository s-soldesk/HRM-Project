package com.hrm.controller;

import com.hrm.dto.ScheduleDto;
import com.hrm.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    /**
     * ✅ 모든 직원의 일정 조회
     */
    @GetMapping
    public List<ScheduleDto> calendarList() {
        return scheduleService.getAllSchedules(); // 모든 일정 가져옴
    }

    /**
     * ✅ 일정 추가 (로그인한 직원 기준)
     */
    @PostMapping("/add")
    public ScheduleDto calendarSave(@RequestBody Map<String, Object> map) {
        ScheduleDto schedule = new ScheduleDto();
        schedule.setEmployeeId(Integer.parseInt((String) map.get("employeeId"))); // 받아온 employeeId 설정
        schedule.setTitle((String) map.get("title"));

        schedule.setStartDate((String) map.get("start"));
        schedule.setEndDate(map.get("end") != null ? (String) map.get("end") : null);

        scheduleService.createSchedule(schedule);
        return schedule; // FullCalendar에서 반영 가능
    }

    /**
     * ✅ 일정 삭제
     */
    @DeleteMapping("/delete/{scheduleId}")
    public String calendarDelete(@PathVariable int scheduleId) {
        try {
            scheduleService.deleteSchedule(scheduleId);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }

    /**
     * ✅ 일정 수정
     */
    @PutMapping("/update/{scheduleId}")
    public String eventUpdate(@PathVariable int scheduleId, @RequestBody Map<String, Object> map) {
        ScheduleDto schedule = new ScheduleDto();
        schedule.setScheduleId(scheduleId);
        schedule.setTitle((String) map.get("title"));
        schedule.setStartDate(map.get("start").toString().substring(0, 19));
        if (map.get("end") != null) {
            schedule.setEndDate(map.get("end").toString().substring(0, 19));
        }

        try {
            scheduleService.updateSchedule(schedule);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }
}
