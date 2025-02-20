// AttendanceController.java
package com.hrm.controller;

import com.hrm.dao.UserAccountDao;
import com.hrm.service.CommuteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/attendance/commute")
public class CommuteController {

    @Autowired
    private CommuteService commuteService;
    
    @Autowired
    private UserAccountDao userAccountDao;
    
    // 출근 기록
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/check_in")
    public String checkIn(@AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
    	// 로그인한 사용자의 이메일 가져오기 (UserAccounts 테이블의 EmployeeID 값)
        String employeeEmail = userDetails.getUsername();

        // 이메일을 이용해 Employee 테이블의 EmployeeID(Integer) 조회
        Integer employeeId = userAccountDao.findEmployeeIdByEmail(employeeEmail);
        
        if (employeeId == null) {
            redirectAttributes.addFlashAttribute("message", "사원 정보를 찾을 수 없습니다.");
            return "redirect:/attendance";
        }
    	
        boolean isCheckInSuccessful = commuteService.recordCheckIn(employeeId);
        String message = isCheckInSuccessful ? "출근이 완료되었습니다." : "이미 출근 기록이 존재합니다.";
        redirectAttributes.addFlashAttribute("message", message);
        
        return "redirect:/attendance";
    }

    // 퇴근 기록
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/check_out")
    public String checkOut(@AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
    	// 로그인한 사용자의 이메일 가져오기 (UserAccounts 테이블의 EmployeeID 값)
        String employeeEmail = userDetails.getUsername();

        // 이메일을 이용해 Employee 테이블의 EmployeeID(Integer) 조회
        Integer employeeId = userAccountDao.findEmployeeIdByEmail(employeeEmail);
        
        if (employeeId == null) {
            redirectAttributes.addFlashAttribute("message", "사원 정보를 찾을 수 없습니다.");
            return "redirect:/attendance";
        }
        
        if (commuteService.hasCheckOutRecord(employeeId)) {
            redirectAttributes.addFlashAttribute("message", "이미 퇴근 기록이 존재합니다.");
            return "redirect:/attendance";
        }

        boolean isCheckOutSuccessful = commuteService.recordCheckOut(employeeId);
        String message = isCheckOutSuccessful ? "퇴근이 완료되었습니다." : "출근 기록이 없습니다. 퇴근 기록을 추가할 수 없습니다.";
        redirectAttributes.addFlashAttribute("message", message);
        
        return "redirect:/attendance";
    }
}
