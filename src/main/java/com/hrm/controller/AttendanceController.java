package com.hrm.controller;

import com.hrm.dao.UserAccountDao;
import com.hrm.dto.AttendanceDto;
import com.hrm.service.AttendanceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


@Controller
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;
    
    @Autowired
    private UserAccountDao userAccountDao;
    
    
    // 근태 관리 메인 페이지
    @GetMapping
    public String showAttendanceMainPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
    	// 로그인한 사용자의 이메일 가져오기
        String employeeEmail = userDetails.getUsername();

        // 로그인한 사용자의 권한 확인
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        // Admin 계정은 employeeId 조회를 하지 않음
        if (!isAdmin) {
            Integer employeeId = userAccountDao.findEmployeeIdByEmail(employeeEmail);

            if (employeeId == null) {
                model.addAttribute("message", "사원 정보를 찾을 수 없습니다.");
            } else {
                model.addAttribute("employeeId", employeeId);
            }
        } else {
            // Admin 계정은 employeeId가 필요 없음
            model.addAttribute("employeeId", "");  // 빈 값으로 설정
        }
    	
    	return "attendance/attendance";
    }
    
    // 근태 기록 조회 페이지
    @GetMapping("/records")
    public String getRecordsPage(@RequestParam(value = "employeeId", required = false) String employeeId,
                                 @RequestParam(value = "name", required = false) String name,
                                 @RequestParam(value = "startDate", required = false) String startDate,
                                 @RequestParam(value = "endDate", required = false) String endDate,
                                 @RequestParam(value = "status", required = false) String status,
                                 Model model,
                                 Authentication authentication) {
    	
    	String loggedInEmployeeEmail = authentication.getName(); // 현재 로그인한 이메일 (UserAccounts.EmployeeID)

        // Employee 권한 사용자는 본인의 Employee 테이블의 EmployeeID(Integer)만 조회 가능
        if (authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_EMPLOYEE"))) {
            Integer employeeDbId = userAccountDao.findEmployeeIdByEmail(loggedInEmployeeEmail);
            if (employeeDbId != null) {
                employeeId = String.valueOf(employeeDbId); // Employee 테이블의 EmployeeID(Integer) 설정
            }
        }
    	
        // 빈 문자열을 null로 변환
        if (startDate != null && startDate.isEmpty()) startDate = null;
        if (endDate != null && endDate.isEmpty()) endDate = null;
        if (employeeId != null && employeeId.isEmpty()) employeeId = null;
        if (name != null && name.isEmpty()) name = null;
        if (status != null && status.isEmpty()) status = null;
        

        // 근태 기록 조회
        List<AttendanceDto> records = attendanceService.searchAttendanceRecords(employeeId, name, startDate, endDate, status);
        
        model.addAttribute("records", records);
        model.addAttribute("employeeId", employeeId);
        
        return "attendance/attendance";
    }
    
    // 근태 기록 수정 페이지
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    @GetMapping("/update/{attendanceId}")
    public String getUpdatePage(@PathVariable("attendanceId") int attendanceId, Model model) {
        AttendanceDto attendance = attendanceService.getAttendanceById(attendanceId);
        if (attendance == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "근태 기록을 찾지 못했습니다");
        }
        model.addAttribute("attendance", attendance);
        return "attendance/update";  
    }


    // 근태 기록 수정 처리
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    @PostMapping("/update")
    public String updateAttendance(@ModelAttribute AttendanceDto attendance, RedirectAttributes redirectAttributes) {
        boolean updated = attendanceService.updateAttendance(
            attendance.getEmployeeId(), 
            attendance.getDate(), 
            attendance.getCheckInTime(), 
            attendance.getCheckOutTime(),
            attendance.getStatus()
        );

        if (!updated) {
            redirectAttributes.addFlashAttribute("error", "근태 기록을 업데이트할 수 없습니다.");
        } else {
            redirectAttributes.addFlashAttribute("message", "근태 기록이 성공적으로 수정되었습니다.");
        }
        
        return "redirect:/attendance/records";
    }
}
