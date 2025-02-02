package com.hrm.controller;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hrm.dto.AttendanceDto;
import com.hrm.dto.SalaryDto;
import com.hrm.service.AttendanceService;
import com.hrm.service.SalaryService;

@Controller
@RequestMapping("/salary")
public class SalaryCalculateController {

	@Autowired
	private SalaryService salaryService;

	@Autowired
	private AttendanceService attendanceService;

	// 급여 계산 페이지
	@GetMapping("/calculate")
	public String calculatePage(Model m) {
		// 최근 12개월 목록 생성
		List<String> months = getRecentMonths(12);
		m.addAttribute("months", months);

		// 기본값 설정
		m.addAttribute("canCloseAttendance", false);
		m.addAttribute("attendanceClosed", false);
		m.addAttribute("salaryCalculated", false);
		return "salary/calculate";
	}

	// 특정 월 급여 계산 현황 조회
	@GetMapping("/calculate/status/{yearMonth}")
	public String getCalculationStatus(@PathVariable("yearMonth") String yearMonth, Model m) {
		// 월별 사원 근태 통계 조회
		List<AttendanceDto> monthlySummary = attendanceService.getMonthlyAttendanceSummary(yearMonth);
		m.addAttribute("attendances", monthlySummary);
		m.addAttribute("selectedMonth", yearMonth);
		m.addAttribute("months", getRecentMonths(12));
		return "salary/calculate";
	}
	
	// 사원별 월간 상세 페이지
    @GetMapping("/calculate/detail/{employeeId}/{yearMonth}")
    public String getEmployeeMonthlyDetail(
            @PathVariable("employeeId") Integer employeeId,
            @PathVariable("yearMonth") String yearMonth,
            Model m) {
        List<AttendanceDto> details = attendanceService.getEmployeeMonthlyAttendance(employeeId, yearMonth);
        
        SalaryDto salaryInfo = salaryService.getEmployeeSalaryByMonth(employeeId, yearMonth);

        // 총 근무 시간 및 초과 근무 시간 계산
        double totalWorkHours = details.stream().mapToDouble(AttendanceDto::getHoursWorked).sum();
        double totalOvertimeHours = details.stream().mapToDouble(AttendanceDto::getOvertimeHours).sum();
        
        // 근태 마감 상태 확인
        boolean isAttendanceClosed = attendanceService.isAttendanceClosed(yearMonth);
        
        m.addAttribute("attendanceClosed", isAttendanceClosed);
        m.addAttribute("attendances", details);
        m.addAttribute("salaryInfo", salaryInfo);
        m.addAttribute("totalWorkHours", totalWorkHours);
        m.addAttribute("totalOvertimeHours", totalOvertimeHours);
        m.addAttribute("yearMonth", yearMonth);
        m.addAttribute("employeeId", employeeId);
        return "salary/calculateDetail";
    }
    
	// 근태 마감
	@PostMapping("/calculate/close/{yearMonth}")
	@ResponseBody
	public ResponseEntity<?> closeAttendance(@PathVariable String yearMonth) {
		try {
			attendanceService.closeAttendance(yearMonth);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	// 급여 계산
	@PostMapping("/calculate/{yearMonth}")
	@ResponseBody
	public ResponseEntity<?> calculateSalaries(@PathVariable String yearMonth) {
		try {
			salaryService.calculateSalaries(yearMonth);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	// 급여 확정
	@PostMapping("/calculate/confirm/{yearMonth}")
	@ResponseBody
	public ResponseEntity<?> confirmSalaries(@PathVariable String yearMonth) {
		try {
			salaryService.confirmSalaries(yearMonth);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	private List<String> getRecentMonths(int count) {
		List<String> months = new ArrayList<>();
		YearMonth current = YearMonth.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM");

		for (int i = 0; i < count; i++) {
			months.add(current.minusMonths(i).format(formatter));
		}
		return months;
	}
}
