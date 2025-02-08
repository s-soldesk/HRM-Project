package com.hrm.controller;

import java.math.BigDecimal;
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
import com.hrm.dto.SalaryDto;
import com.hrm.service.AttendanceService;
import com.hrm.service.SalaryService;

@Controller
@RequestMapping("/salary")
@PreAuthorize("hasAnyRole('HR', 'ADMIN')")  // 클래스 레벨에서 권한 체크
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
    public String getEmployeeMonthlyDetail(
            @PathVariable("employeeId") Integer employeeId,
            @PathVariable("yearMonth") String yearMonth, 
            Model model) {
        
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
    public ResponseEntity<?> confirmAttendance(
            @PathVariable("employeeId") Integer employeeId,
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
    public ResponseEntity<?> processSalary(
            @PathVariable("employeeId") Integer employeeId,
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