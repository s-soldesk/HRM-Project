package com.hrm.controller;

import com.hrm.dto.ScheduleDto;
import com.hrm.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/attendance/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    // 일정 관리 페이지 렌더링
    @GetMapping
    public String showSchedulePage(Model model) {
        List<ScheduleDto> schedules = scheduleService.getAllSchedules();
        model.addAttribute("schedules", schedules);
        return "schedule"; 
    }


    // 일정 추가 (근무 일정 또는 휴가 신청)
    @PostMapping("/add")
    public String addSchedule(@ModelAttribute ScheduleDto scheduleDto, Model model) {
        boolean isAdded = scheduleService.addSchedule(scheduleDto);
        if (isAdded) {
            model.addAttribute("message", "일정이 성공적으로 추가되었습니다.");
        } else {
            model.addAttribute("message", "일정 추가에 실패하였습니다.");
        }
        return "redirect:/attendance/schedule";
    }


    // 사원의 일정 조회 (사원 ID 기준)
    @GetMapping("/{employeeId}")
    public String getEmployeeSchedules(@PathVariable("employeeId") int employeeId, Model model) {
        List<ScheduleDto> schedules = scheduleService.getSchedulesByEmployee(employeeId);
        model.addAttribute("schedules", schedules);
        return "schedule";
    }

    // 일정 승인/거절 (HR 관리자용)
    @PostMapping("/approve")
    public String approveSchedule(@RequestParam("scheduleId") int scheduleId, @RequestParam("status") String status, Model model) {
        boolean isUpdated = scheduleService.updateScheduleStatus(scheduleId, status);
        if (isUpdated) {
            model.addAttribute("message", "일정 상태가 변경되었습니다.");
        } else {
            model.addAttribute("message", "일정 상태 변경에 실패하였습니다.");
        }
        return "redirect:/attendance/schedule";
    }


    // 일정 삭제
    @PostMapping("/delete/{scheduleId}")
    public String deleteSchedule(@PathVariable("scheduleId") int scheduleId, Model model) {
        boolean isDeleted = scheduleService.deleteSchedule(scheduleId);
        if (isDeleted) {
            model.addAttribute("message", "일정이 삭제되었습니다.");
        } else {
            model.addAttribute("message", "일정 삭제에 실패하였습니다.");
        }
        return "redirect:/attendance/schedule";
    }
}
