// AttendanceController.java
package com.hrm.controller;

import com.hrm.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @GetMapping
    public String showAttendancePage() {
        return "attendance";
    }

    @PostMapping("/check-in")
    public String checkIn(@RequestParam("employeeId") int employeeId, Model model) {
        boolean isCheckInSuccessful = attendanceService.recordCheckIn(employeeId);
        if (isCheckInSuccessful) {
            model.addAttribute("message", "출근이 완료되었습니다.");
        } else {
            model.addAttribute("message", "이미 출근 기록이 존재합니다.");
        }
        return "attendance";
    }

    @PostMapping("/check-out")
    public String checkOut(@RequestParam("employeeId") int employeeId, Model model) {
        boolean isCheckOutSuccessful = attendanceService.recordCheckOut(employeeId);
        if (isCheckOutSuccessful) {
            model.addAttribute("message", "퇴근이 완료되었습니다.");
        } else {
            model.addAttribute("message", "출근 기록이 없습니다. 퇴근 기록을 추가할 수 없습니다.");
        }
        return "attendance";
    }
}
