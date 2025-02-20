package com.hrm.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.hrm.dto.AttendanceDto;
import com.hrm.dto.EmployeeDto;
import com.hrm.dto.SalaryDto;
import com.hrm.service.AttendanceService;
import com.hrm.service.SalaryService;

@Controller
@RequestMapping("/salary")
@PreAuthorize("hasAnyRole('HR', 'ADMIN')") // 클래스 레벨에서 권한 체크
public class SalaryCalculateController {

	@Autowired
	private SalaryService salaryService;

	@Autowired
	private AttendanceService attendanceService;

	// 급여 계산 페이지
	@GetMapping("/calculate")
	public String calculatePage(Model model) {
		List<String> months = getRecentMonths(12);
		model.addAttribute("months", months);
		model.addAttribute("canCloseAttendance", false);
		model.addAttribute("attendanceClosed", false);
		model.addAttribute("salaryCalculated", false);
		return "salary/calculate";
	}

	// 특정 월 급여 계산 현황 조회
	@GetMapping("/calculate/status/{yearMonth}")
	public String getCalculationStatus(@PathVariable("yearMonth") String yearMonth, Model model) {
		List<AttendanceDto> monthlySummary = attendanceService.getMonthlyAttendanceSummary(yearMonth);

		for (AttendanceDto attendance : monthlySummary) {
			SalaryDto salary = salaryService.getEmployeeSalaryByMonth(attendance.getEmployeeId(), yearMonth);
			attendance.setSalaryStatus(salary != null ? salary.getStatus() : "NOT_CALCULATED");
		}

		model.addAttribute("attendances", monthlySummary);
		model.addAttribute("selectedMonth", yearMonth);
		model.addAttribute("months", getRecentMonths(12));
		return "salary/calculate";
	}

	// 사원별 월간 상세 페이지
	@GetMapping("/calculate/detail/{employeeId}/{yearMonth}")
	public String getEmployeeMonthlyDetail(@PathVariable("employeeId") Integer employeeId,
			@PathVariable("yearMonth") String yearMonth, Model model) {

		List<AttendanceDto> details = attendanceService.getEmployeeMonthlyAttendance(employeeId, yearMonth);
		if (details.isEmpty()) {
			return "redirect:/salary/calculate?error=noAttendanceData";
		}

		SalaryDto salaryInfo = salaryService.getEmployeeSalaryByMonth(employeeId, yearMonth);
		if (salaryInfo == null) {
			salaryInfo = createEmptySalaryDto(employeeId);
		}

		double totalWorkHours = details.stream().mapToDouble(AttendanceDto::getHoursWorked).sum();
		double totalOvertimeHours = details.stream().mapToDouble(AttendanceDto::getOvertimeHours).sum();

		model.addAttribute("attendances", details);
		model.addAttribute("salaryInfo", salaryInfo);
		model.addAttribute("totalWorkHours", totalWorkHours);
		model.addAttribute("totalOvertimeHours", totalOvertimeHours);
		model.addAttribute("yearMonth", yearMonth);
		model.addAttribute("employeeId", employeeId);
		return "salary/calculateDetail";
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
			SalaryDto salary = salaryService.getEmployeeSalaryByMonth(employeeId, yearMonth);
			if (salary == null || !"CONFIRMED".equals(salary.getStatus())) {
				return ResponseEntity.badRequest().body("급여가 먼저 확정되어야 합니다.");
			}

			salaryService.calculateSalaries(employeeId, yearMonth);
			return ResponseEntity.ok().body("급여 계산이 완료되었습니다.");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("오류 발생: " + e.getMessage());
		}
	}

	private SalaryDto createEmptySalaryDto(Integer employeeId) {
		SalaryDto salaryInfo = new SalaryDto();
		salaryInfo.setEmployeeId(employeeId);
		salaryInfo.setBaseSalary(BigDecimal.ZERO);
		salaryInfo.setMealAllowance(BigDecimal.ZERO);
		salaryInfo.setPositionAllowance(BigDecimal.ZERO);
		salaryInfo.setOvertimePay(BigDecimal.ZERO);
		salaryInfo.setDeductionTotal(BigDecimal.ZERO);
		salaryInfo.setNetPay(BigDecimal.ZERO);
		return salaryInfo;
	}

	// 급여데이터 임의 초기화
	@PostMapping("/calculate/initialize/{employeeId}/{yearMonth}")
	@ResponseBody
	public ResponseEntity<?> initializeSalary(@PathVariable("employeeId") Integer employeeId,
			@PathVariable("yearMonth") String yearMonth) {
		try {
			// 날짜 처리
			String[] parts = yearMonth.split("-");
			LocalDate firstDayOfMonth = LocalDate.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), 1);

			// 새로운 급여 정보 생성
			SalaryDto newSalary = new SalaryDto();
			newSalary.setEmployeeId(employeeId);
			newSalary.setPaymentDate(firstDayOfMonth);

			// 기본 금액 설정
			BigDecimal baseSalary = new BigDecimal("3000000");
			BigDecimal mealAllowance = new BigDecimal("200000");
			BigDecimal positionAllowance = new BigDecimal("200000");
			BigDecimal overtimePay = BigDecimal.ZERO;

			newSalary.setBaseSalary(baseSalary);
			newSalary.setMealAllowance(mealAllowance);
			newSalary.setPositionAllowance(positionAllowance);
			newSalary.setOvertimePay(overtimePay);

			// 총 급여 계산 (기본급 + 식대 + 직책수당)
			BigDecimal totalSalary = baseSalary.add(mealAllowance).add(positionAllowance).add(overtimePay);
			newSalary.setTotalSalary(totalSalary);

			// 공제액 초기화
			newSalary.setIncomeTax(BigDecimal.ZERO);
			newSalary.setLocalIncomeTax(BigDecimal.ZERO);
			newSalary.setNationalPension(BigDecimal.ZERO);
			newSalary.setHealthInsurance(BigDecimal.ZERO);
			newSalary.setEmploymentInsurance(BigDecimal.ZERO);
			newSalary.setLongTermCareInsurance(BigDecimal.ZERO);

			newSalary.setStatus("PENDING");

			salaryService.addSalary(newSalary);
			return ResponseEntity.ok().body("급여 정보가 초기화되었습니다.");

		} catch (Exception e) {
			return ResponseEntity.badRequest().body("급여 초기화 중 오류가 발생했습니다: " + e.getMessage());
		}
	}

	@GetMapping("/manual-edit/{employeeId}/{yearMonth}")
	@PreAuthorize("hasAnyRole('HR', 'ADMIN')")
	public String showManualEditForm(@PathVariable(name = "employeeId") Integer employeeId,
			@PathVariable(name = "yearMonth") String yearMonth, Model model) {
		try {
			// yearMonth 형식 통일 (yyyy-MM)
			yearMonth = yearMonth.replace("/", "-");

			// 해당 월의 급여 정보 조회
			SalaryDto salary = salaryService.getEmployeeSalaryByMonth(employeeId, yearMonth);

			if (salary == null) {
				return "redirect:/salary/calculate/status/" + yearMonth;
			}

			// salary에서 employee 정보를 가져옴
			EmployeeDto employee = salary.getEmployee();

			if (employee == null) {
				return "redirect:/salary/calculate/status/" + yearMonth;
			}

			model.addAttribute("employee", employee);
			model.addAttribute("salary", salary);
			model.addAttribute("yearMonth", yearMonth);

			return "salary/salaryManualEdit";
		} catch (Exception e) {
			return "redirect:/salary/calculate/status/" + yearMonth;
		}
	}

	@PostMapping("/manual-edit/save")
	@PreAuthorize("hasAnyRole('HR', 'ADMIN')")
	public String saveManualEdit(@ModelAttribute SalaryDto salaryDto) {
		try {
			// 기존 급여 정보 조회
			SalaryDto existingSalary = salaryService.getSalaryById(salaryDto.getSalaryId());
			if (existingSalary == null) {
				return "redirect:/salary/calculate/status/" + salaryDto.getYearMonth();
			}

			// 수정된 값만 업데이트하고 나머지는 기존 값 유지
			existingSalary.setBaseSalary(salaryDto.getBaseSalary());
			existingSalary.setMealAllowance(salaryDto.getMealAllowance());
			existingSalary.setPositionAllowance(salaryDto.getPositionAllowance());
			existingSalary.setOvertimePay(salaryDto.getOvertimePay());

			// 공제 항목 업데이트
			existingSalary.setIncomeTax(salaryDto.getIncomeTax());
			existingSalary.setLocalIncomeTax(salaryDto.getLocalIncomeTax());
			existingSalary.setNationalPension(salaryDto.getNationalPension());
			existingSalary.setHealthInsurance(salaryDto.getHealthInsurance());
			existingSalary.setEmploymentInsurance(salaryDto.getEmploymentInsurance());
			existingSalary.setLongTermCareInsurance(salaryDto.getLongTermCareInsurance());

			// 총액 계산
			BigDecimal totalSalary = existingSalary.getBaseSalary().add(existingSalary.getMealAllowance())
					.add(existingSalary.getPositionAllowance()).add(existingSalary.getOvertimePay());
			existingSalary.setTotalSalary(totalSalary);

			// 저장
			salaryService.updateSalary(existingSalary);

			return "redirect:/salary/calculate/detail/" + existingSalary.getEmployeeId() + "/"
					+ existingSalary.getYearMonth();
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/salary/calculate/status/" + salaryDto.getYearMonth();
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