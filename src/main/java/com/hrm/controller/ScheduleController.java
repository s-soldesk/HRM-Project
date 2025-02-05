package com.hrm.controller;

import com.hrm.dto.ScheduleDto;
import com.hrm.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/attendance/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    // 휴가 신청 페이지 렌더링
    @GetMapping("/add")
    public String showAddSchedulePage(Model model) {
        model.addAttribute("scheduleDto", new ScheduleDto());
        return "schedule_add";  // 휴가 신청 페이지
    }

    // 일정 추가 처리 (휴가 신청)
    @PostMapping("/add")
    public String addSchedule(@ModelAttribute ScheduleDto scheduleDto, RedirectAttributes redirectAttributes) {
        boolean isAdded = scheduleService.addSchedule(scheduleDto);
        redirectAttributes.addFlashAttribute("message", isAdded ? "일정이 성공적으로 추가되었습니다." : "일정 추가에 실패하였습니다.");
        return "redirect:/attendance/schedule/list";  // 신청 후 목록 페이지로 리다이렉트
    }

    // 휴가 신청 목록 페이지 렌더링
    @GetMapping("/list")
    public String showScheduleListPage(Model model) {
        List<ScheduleDto> schedules = scheduleService.getAllSchedules();
        model.addAttribute("schedules", schedules);
        return "schedule_list";  // 휴가 신청 목록 페이지
    }

    // 특정 사원의 일정 조회
    @GetMapping("/{employeeId}")
    public String getEmployeeSchedules(@PathVariable("employeeId") int employeeId, Model model) {
        List<ScheduleDto> schedules = scheduleService.getSchedulesByEmployee(employeeId);
        model.addAttribute("schedules", schedules);
        return "schedule_list";
    }

    // 일정 승인/거절 처리 (HR 관리자용)
    @PostMapping("/approve")
    public String approveSchedule(@RequestParam("scheduleId") int scheduleId, @RequestParam("status") String status, RedirectAttributes redirectAttributes) {
        boolean isUpdated = scheduleService.updateScheduleStatus(scheduleId, status);
        redirectAttributes.addFlashAttribute("message", isUpdated ? "일정 상태가 변경되었습니다." : "일정 상태 변경에 실패하였습니다.");
        return "redirect:/attendance/schedule/list";
    }

    // 일정 삭제 처리
    @PostMapping("/delete/{scheduleId}")
    public String deleteSchedule(@PathVariable("scheduleId") int scheduleId, RedirectAttributes redirectAttributes) {
        boolean isDeleted = scheduleService.deleteSchedule(scheduleId);
        redirectAttributes.addFlashAttribute("message", isDeleted ? "일정이 삭제되었습니다." : "일정 삭제에 실패하였습니다.");
        return "redirect:/attendance/schedule/list";
    }
}
