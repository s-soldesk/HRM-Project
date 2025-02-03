// AttendanceController.java
package com.hrm.controller;

import com.hrm.service.CommuteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/attendance/commute")
public class CommuteController {

    @Autowired
    private CommuteService attendanceService;
    
    // 출퇴근 기록 페이지
    @GetMapping
    public String showCommutePage() {
        return "commute";
    }

    // 출근 기록
    @PostMapping("/check-in")
    public String checkIn(@RequestParam("employeeId") int employeeId, Model model) {
        boolean isCheckInSuccessful = attendanceService.recordCheckIn(employeeId);
        if (isCheckInSuccessful) {
            model.addAttribute("message", "출근이 완료되었습니다.");
        } else {
            model.addAttribute("message", "이미 출근 기록이 존재합니다.");
        }
        return "commute";
    }

    // 퇴근 기록
    @PostMapping("/check-out")
    public String checkOut(@RequestParam("employeeId") int employeeId, Model model) {
    	 // 이미 퇴근 기록이 있는지 확인
        if (attendanceService.hasCheckOutRecord(employeeId)) {
            model.addAttribute("message", "이미 퇴근 기록이 존재합니다.");
            return "commute";
        }
        
        // 출근 기록이 있는지 확인 후 퇴근 기록 추가
        boolean isCheckOutSuccessful = attendanceService.recordCheckOut(employeeId);
        if (isCheckOutSuccessful) {
            model.addAttribute("message", "퇴근이 완료되었습니다.");
        } else {
            model.addAttribute("message", "출근 기록이 없습니다. 퇴근 기록을 추가할 수 없습니다.");
        }
        return "commute";
    }
}
