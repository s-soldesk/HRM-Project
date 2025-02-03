package com.hrm.controller;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

		// 급여 상태 조회 및 설정
		for (AttendanceDto attendance : monthlySummary) {
			SalaryDto salary = salaryService.getEmployeeSalaryByMonth(attendance.getEmployeeId(), yearMonth);
			attendance.setSalaryStatus(salary != null ? salary.getStatus() : "NOT_CALCULATED");
		}

		m.addAttribute("attendances", monthlySummary);
		m.addAttribute("selectedMonth", yearMonth);
		m.addAttribute("months", getRecentMonths(12));
		return "salary/calculate";
	}

	// 사원별 월간 상세 페이지
	@GetMapping("/calculate/detail/{employeeId}/{yearMonth}")
	public String getEmployeeMonthlyDetail(@PathVariable("employeeId") Integer employeeId,
			@PathVariable("yearMonth") String yearMonth, Model m) {
		List<AttendanceDto> details = attendanceService.getEmployeeMonthlyAttendance(employeeId, yearMonth);

		if (details.isEmpty()) {
			// 데이터가 없을 경우 처리
			return "redirect:/salary/calculate?error=noAttendanceData";
		}

		SalaryDto salaryInfo = salaryService.getEmployeeSalaryByMonth(employeeId, yearMonth);

		// salaryInfo가 null일 경우 새 SalaryDto 생성
		if (salaryInfo == null) {
			salaryInfo = new SalaryDto();
			salaryInfo.setEmployeeId(employeeId);
			salaryInfo.setBaseSalary(BigDecimal.ZERO);
			salaryInfo.setMealAllowance(BigDecimal.ZERO);
			salaryInfo.setPositionAllowance(BigDecimal.ZERO);
			salaryInfo.setOvertimePay(BigDecimal.ZERO);
			salaryInfo.setDeductionTotal(BigDecimal.ZERO);
			salaryInfo.setNetPay(BigDecimal.ZERO);
		}

		// 총 근무 시간 및 초과 근무 시간 계산
		double totalWorkHours = details.stream().mapToDouble(AttendanceDto::getHoursWorked).sum();
		double totalOvertimeHours = details.stream().mapToDouble(AttendanceDto::getOvertimeHours).sum();

		// 근태 마감 상태 확인
		boolean isSalaryClosed = salaryService.isSalaryClosed(employeeId, yearMonth);

		m.addAttribute("attendanceClosed", isSalaryClosed);
		m.addAttribute("attendances", details);
		m.addAttribute("salaryInfo", salaryInfo);
		m.addAttribute("totalWorkHours", totalWorkHours);
		m.addAttribute("totalOvertimeHours", totalOvertimeHours);
		m.addAttribute("yearMonth", yearMonth);
		m.addAttribute("employeeId", employeeId);
		return "salary/calculateDetail";
	}

	// 급여 계산
	@PostMapping("/calculate/{employeeId}/{yearMonth}")
	@ResponseBody
	public ResponseEntity<?> calculateSalaries(@PathVariable("employeeId") Integer employeeId,
			@PathVariable("yearMonth") String yearMonth) {
		try {
			salaryService.calculateSalaries(employeeId, yearMonth);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	// 근태 확정
	@PostMapping("/calculate/confirm/{employeeId}/{yearMonth}")
	@ResponseBody
	public ResponseEntity<?> confirmAttendance(@PathVariable("employeeId") Integer employeeId,
			@PathVariable("yearMonth") String yearMonth) {
		try {
			salaryService.confirmSalaries(employeeId.toString(), yearMonth);
			return ResponseEntity.ok().body("근태가 확정되었습니다.");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	// 급여 계산
	@PostMapping("/calculate/salary/{employeeId}/{yearMonth}")
	@ResponseBody
	public ResponseEntity<?> processSalary(@PathVariable("employeeId") Integer employeeId,
			@PathVariable("yearMonth") String yearMonth) {
		try {
			// 급여 상태가 확정인지 확인
			SalaryDto salary = salaryService.getEmployeeSalaryByMonth(employeeId, yearMonth);
			if (salary == null || !"CONFIRMED".equals(salary.getStatus())) {
				return ResponseEntity.badRequest().body("급여가 먼저 확정되어야 합니다.");
			}

			// 급여 계산 처리
			salaryService.calculateSalaries(employeeId, yearMonth);
			return ResponseEntity.ok().body("급여 계산이 완료되었습니다.");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("오류 발생: " + e.getMessage());
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
