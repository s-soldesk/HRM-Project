package com.hrm.controller;

import com.hrm.dto.AttendanceDto;
import com.hrm.service.AttendanceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


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
    
    
    // 근태 기록 조회 페이지
    @GetMapping("/records")
    public String getRecordsPage(@RequestParam(value = "employeeId", required = false) String employeeId,
                                 @RequestParam(value = "name", required = false) String name,
                                 @RequestParam(value = "startDate", required = false) String startDate,
                                 @RequestParam(value = "endDate", required = false) String endDate,
                                 @RequestParam(value = "attendanceType", required = false) String attendanceType,
                                 Model model) {

        // 빈 문자열을 null로 변환
        if (startDate != null && startDate.isEmpty()) startDate = null;
        if (endDate != null && endDate.isEmpty()) endDate = null;
        if (employeeId != null && employeeId.isEmpty()) employeeId = null;
        if (name != null && name.isEmpty()) name = null;
        if (attendanceType != null && attendanceType.isEmpty()) attendanceType = null;

        // 근태 및 휴가 기록 조회
        List<AttendanceDto> records = attendanceService.searchAttendanceRecords(employeeId, name, startDate, endDate, attendanceType);
        model.addAttribute("records", records);
        return "attendance";
    }
    
    // 근태 기록 수정 페이지
    @GetMapping("/update/{attendanceId}")
    public String getUpdatePage(@PathVariable("attendanceId") int attendanceId, Model model) {
        AttendanceDto attendance = attendanceService.getAttendanceById(attendanceId);
        if (attendance == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "근태 기록을 찾지 못했습니다");
        }
        model.addAttribute("attendance", attendance);
        return "update";
    }

    // 근태 기록 수정 처리
    @PostMapping("/update")
    public String updateAttendance(@ModelAttribute AttendanceDto attendance) {
        attendanceService.updateAttendance(attendance);
        return "redirect:/attendance/records";
    }
    
    // PDF 다운로드 메서드 추가
    @GetMapping("/records/pdf")
    public ResponseEntity<byte[]> generatePdfReport(@RequestParam(value = "employeeId", required = false) String employeeId,
                                                    @RequestParam(value = "name", required = false) String name,
                                                    @RequestParam(value = "startDate", required = false) String startDate,
                                                    @RequestParam(value = "endDate", required = false) String endDate,
                                                    @RequestParam(value = "attendanceType", required = false) String attendanceType) {
        // PDF 생성 로직
        List<AttendanceDto> records = attendanceService.searchAttendanceRecords(employeeId, name, startDate, endDate, attendanceType);
        byte[] pdfData = attendanceService.generatePdf(records);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=attendance_report.pdf");
        headers.add("Content-Type", "application/pdf");

        return ResponseEntity.ok().headers(headers).body(pdfData);
    }
}
