package com.hrm.controller;

import com.hrm.dto.ScheduleDto;
import com.hrm.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/attendance/leave")
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    // 휴가 신청 페이지 렌더링
    @GetMapping("/add")
    public String showAddLeavePage(Model model) {
        model.addAttribute("scheduleDto", new ScheduleDto());
        return "leave_add";  // 휴가 신청 페이지
    }

    // 휴가 신청 처리
    @PostMapping("/add")
    public String addLeave(@ModelAttribute ScheduleDto scheduleDto, RedirectAttributes redirectAttributes) {
        boolean isAdded = leaveService.addLeave(scheduleDto);
        redirectAttributes.addFlashAttribute("message", isAdded ? "휴가가 성공적으로 추가되었습니다." : "휴가 추가에 실패하였습니다.");
        return "redirect:/attendance/leave/list";  // 신청 후 목록 페이지로 리다이렉트
    }

    // 휴가 신청 목록 페이지 렌더링
    @GetMapping("/list")
    public String showLeaveListPage(Model model) {
        List<ScheduleDto> schedules = leaveService.getAllLeaves();
        model.addAttribute("leaves", schedules);
        return "leave_list";  // 휴가 신청 목록 페이지
    }

    // 특정 사원의 휴가 일정 조회
    @GetMapping("/{employeeId}")
    public String getEmployeeLeaves(@PathVariable("employeeId") int employeeId, Model model) {
        List<ScheduleDto> schedules = leaveService.getLeavesByEmployee(employeeId);
        model.addAttribute("schedules", schedules);
        return "leave_list";
    }

    // 휴가 승인/거절 처리 (HR 관리자용)
    @PostMapping("/approve")
    public String approveLeave(@RequestParam("scheduleId") int scheduleId, @RequestParam("status") String status, RedirectAttributes redirectAttributes) {
        boolean isUpdated = leaveService.updateLeaveStatus(scheduleId, status);
        redirectAttributes.addFlashAttribute("message", isUpdated ? "휴가 상태가 변경되었습니다." : "휴가 상태 변경에 실패하였습니다.");
        return "redirect:/attendance/leave/list";
    }

    // 휴가 삭제 처리
    @PostMapping("/delete/{leaveId}")
    public String deleteLeave(@PathVariable("leaveId") int leaveId, RedirectAttributes redirectAttributes) {
        boolean isDeleted = leaveService.deleteLeave(leaveId);
        redirectAttributes.addFlashAttribute("message", isDeleted ? "휴가가 삭제되었습니다." : "휴가 삭제에 실패하였습니다.");
        return "redirect:/attendance/leave/list";
    }
}
