package com.hrm.controller;

import com.hrm.dao.UserAccountDao;
import com.hrm.dto.ScheduleDto;
import com.hrm.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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

    @Autowired
    private UserAccountDao userAccountDao;
    
    // 휴가 신청 페이지 렌더링
    @GetMapping("/add")
    public String showAddLeavePage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
    	ScheduleDto scheduleDto = new ScheduleDto();

        // 로그인한 사용자의 이메일 가져오기
        String employeeEmail = userDetails.getUsername();

        // 이메일을 이용해 Employee 테이블의 EmployeeID(Integer) 조회
        Integer employeeId = userAccountDao.findEmployeeIdByEmail(employeeEmail);

        if (employeeId == null) {
            model.addAttribute("message", "사원 정보를 찾을 수 없습니다.");
        } else {
        	scheduleDto.setEmployeeId(String.valueOf(employeeId));
        }
        
    	model.addAttribute("scheduleDto", new ScheduleDto());
        return "attendance/leave_add";  // 휴가 신청 페이지
    }

    // 휴가 신청 처리
    @PostMapping("/add")
    public String addLeave(@ModelAttribute ScheduleDto scheduleDto, 
                           @AuthenticationPrincipal UserDetails userDetails,
                           RedirectAttributes redirectAttributes) {

    	 // 로그인한 사용자의 이메일 가져오기
        String employeeEmail = userDetails.getUsername();

        // 이메일을 이용해 Employee 테이블의 EmployeeID(Integer) 조회
        Integer employeeId = userAccountDao.findEmployeeIdByEmail(employeeEmail);

        if (employeeId == null) {
            redirectAttributes.addFlashAttribute("message", "사원 정보를 찾을 수 없습니다.");
            return "redirect:/attendance/leave/add";
        }

        // Employee 권한일 경우, 본인 ID로만 신청 가능하게 강제 설정
        scheduleDto.setEmployeeId(String.valueOf(employeeId));

        boolean isAdded = leaveService.addLeave(scheduleDto);
        redirectAttributes.addFlashAttribute("message", isAdded ? "휴가가 성공적으로 추가되었습니다." : "휴가 추가에 실패하였습니다.");
        return "redirect:/attendance/leave/list";  // 신청 후 목록 페이지로 리다이렉트
    }

    // 휴가 신청 목록 페이지 렌더링
    @GetMapping("/list")
    public String showLeaveListPage(Model model) {
        List<ScheduleDto> schedules = leaveService.getAllLeaves();
        model.addAttribute("leaves", schedules);
        return "/attendance/leave_list";  // 휴가 신청 목록 페이지
    }
    
    // 휴가 승인 처리 (HR 관리자용)
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    @PostMapping("/approve")
    public String approveLeave(@RequestParam("scheduleId") int scheduleId, RedirectAttributes redirectAttributes) {
        boolean isApproved = leaveService.approveLeave(scheduleId);
        redirectAttributes.addFlashAttribute("message", isApproved ? "휴가가 승인되었습니다." : "휴가 승인에 실패했습니다.");
        return "redirect:/attendance/leave/list";
    }

    // 휴가 거절 처리 (HR 관리자용)
    @PostMapping("/reject")
    public String rejectLeave(@RequestParam("scheduleId") int scheduleId, RedirectAttributes redirectAttributes) {
        boolean isRejected = leaveService.rejectLeave(scheduleId);
        redirectAttributes.addFlashAttribute("message", isRejected ? "휴가가 거절되었습니다." : "휴가 거절에 실패했습니다.");
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
