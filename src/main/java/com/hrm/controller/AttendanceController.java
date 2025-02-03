package com.hrm.controller;

import com.hrm.service.AttendanceService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    // 근태 관리 메인 페이지
    @GetMapping
    public String showAttendanceMainPage() {
        return "attendance";
    }
    
    
}
