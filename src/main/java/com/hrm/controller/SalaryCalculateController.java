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
		// 로깅 추가
		System.out.println("Received yearMonth: " + yearMonth);

		// 단순히 replace 사용
		String formattedYearMonth = yearMonth; // 이미 올바른 형식으로 전달됨

		List<AttendanceDto> attendances = attendanceService.getAllEmployeeAttendance(formattedYearMonth);
		boolean attendanceClosed = attendanceService.isAttendanceClosed(formattedYearMonth);
		boolean salaryCalculated = salaryService.isSalaryCalculated(yearMonth);

		m.addAttribute("attendances", attendances);
		m.addAttribute("attendanceClosed", attendanceClosed);
		m.addAttribute("salaryCalculated", salaryCalculated);
		m.addAttribute("months", getRecentMonths(12));
		m.addAttribute("selectedMonth", yearMonth);

		boolean canCloseAttendance = !attendanceClosed && (attendances != null && !attendances.isEmpty())
				&& attendances.stream().allMatch(a -> "APPROVED".equals(a.getStatus()));
		m.addAttribute("canCloseAttendance", canCloseAttendance);

		if (attendanceClosed) {
			List<SalaryDto> salaries = salaryService.getCalculatedSalaries(yearMonth);
			m.addAttribute("salaries", salaries);
		}

		return "salary/calculate";
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
