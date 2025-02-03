package com.hrm.controller;

import com.hrm.dto.ScheduleDto;
import com.hrm.service.ScheduleService;
import com.hrm.service.UserDetailsServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {
    
    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    
    // 일정 조회
    @GetMapping
    public List<ScheduleDto> getAllSchedules() {
        return scheduleService.getAllSchedules();
    }

    // 일정 추가
    @PostMapping
    public ResponseEntity<String> createSchedule(@RequestBody ScheduleDto scheduleDto, Principal principal) {
        // 로그인한 사용자의 EmployeeID 가져오기
        String employeeId = principal.getName();  // 여기서 employeeId 그대로 가져옴

        scheduleDto.setEmployeeId(Integer.parseInt(employeeId));  // EmployeeID 설정
        scheduleService.createSchedule(scheduleDto);

        return ResponseEntity.ok("일정이 등록되었습니다.");
    }

    // 일정 수정
    @PutMapping("/{scheduleID}")
    public ResponseEntity<String> updateSchedule(@PathVariable int scheduleID, @RequestBody ScheduleDto scheduleDto) {
        scheduleDto.setScheduleId(scheduleID);
        scheduleService.updateSchedule(scheduleDto);
        return ResponseEntity.ok("일정이 수정되었습니다.");
    }

    // 일정 삭제
    @DeleteMapping("/{scheduleID}")
    public ResponseEntity<String> deleteSchedule(@PathVariable int scheduleID) {
        scheduleService.deleteSchedule(scheduleID);
        return ResponseEntity.ok("일정이 삭제되었습니다.");
    }
}
