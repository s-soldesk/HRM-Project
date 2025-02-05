package com.hrm.controller;

import com.hrm.dto.ScheduleDto;
import com.hrm.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    public List<ScheduleDto> getAllSchedules() {
        return scheduleService.getAllSchedules(); // 모든 일정 가져옴
    }

    /**
     * ✅ 일정 추가 (로그인한 직원 기준)
     */
    @PostMapping("/add")
    public ScheduleDto createSchedule(@RequestBody Map<String, Object> map, Principal principal) {
        ScheduleDto schedule = new ScheduleDto();

        // 로그인한 직원의 employeeId 가져오기
        String employeeId = principal.getName();
        schedule.setEmployeeId(Integer.parseInt(employeeId));

        schedule.setTitle((String) map.get("title"));

        // ✅ 날짜 변환 로직 추가
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String startStr = (String) map.get("start");
        String endStr = (String) map.get("end");

        if (startStr.length() == 10) { // 날짜만 있는 경우
            LocalDate startDate = LocalDate.parse(startStr, dateFormatter);
            schedule.setStartDate(startDate.atStartOfDay().format(dateTimeFormatter));
        } else { // 시간까지 포함된 경우
            LocalDateTime startDateTime = LocalDateTime.parse(startStr, dateTimeFormatter);
            schedule.setStartDate(startDateTime.format(dateTimeFormatter));
        }

        if (endStr != null) {
            if (endStr.length() == 10) { // 날짜만 있는 경우
                LocalDate endDate = LocalDate.parse(endStr, dateFormatter);
                schedule.setEndDate(endDate.atStartOfDay().format(dateTimeFormatter));
            } else { // 시간까지 포함된 경우
                LocalDateTime endDateTime = LocalDateTime.parse(endStr, dateTimeFormatter);
                schedule.setEndDate(endDateTime.format(dateTimeFormatter));
            }
        } else {
            schedule.setEndDate(null);
        }

        scheduleService.createSchedule(schedule);
        return schedule;
    }


    /**
     * ✅ 일정 삭제
     */
    @DeleteMapping("/delete/{scheduleId}")
    public String deleteSchedule(@PathVariable("scheduleId") int scheduleId) {
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
    public String updateSchedule(@PathVariable("scheduleId") int scheduleId, @RequestBody Map<String, Object> map) {
        ScheduleDto schedule = new ScheduleDto();
        schedule.setScheduleId(scheduleId);
        schedule.setTitle((String) map.get("title"));

        // 📌 날짜 변환 (ISO 8601 → yyyy-MM-dd HH:mm:ss)
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

        if (map.get("start") != null) {
            ZonedDateTime startUTC = ZonedDateTime.parse(map.get("start").toString(), formatter)
                    .withZoneSameInstant(ZoneId.of("Asia/Seoul"));
            schedule.setStartDate(startUTC.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if (map.get("end") != null) {
            ZonedDateTime endUTC = ZonedDateTime.parse(map.get("end").toString(), formatter)
                    .withZoneSameInstant(ZoneId.of("Asia/Seoul"));
            schedule.setEndDate(endUTC.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
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
