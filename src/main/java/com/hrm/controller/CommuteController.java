// AttendanceController.java
package com.hrm.controller;

import com.hrm.service.CommuteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/attendance/commute")
public class CommuteController {

    @Autowired
    private CommuteService commuteService;
    
    // 출퇴근 기록 페이지
    @GetMapping
    public String showCommutePage() {
        return "commute";
    }

    // 출근 기록
    @PostMapping("/check_in")
    public String checkIn(@RequestParam("employeeId") int employeeId, RedirectAttributes redirectAttributes) {
        boolean isCheckInSuccessful = commuteService.recordCheckIn(employeeId);
        String message = isCheckInSuccessful ? "출근이 완료되었습니다." : "이미 출근 기록이 존재합니다.";
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/attendance";
    }

    // 퇴근 기록
    @PostMapping("/check_out")
    public String checkOut(@RequestParam("employeeId") int employeeId, RedirectAttributes redirectAttributes) {
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
