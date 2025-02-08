package com.hrm.service;

import com.hrm.dao.SalaryDao;
import com.hrm.dto.AttendanceDto;
import com.hrm.dto.SalaryDto;
import com.hrm.utils.AllowancePolicy;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SalaryServiceImpl implements SalaryService {

	private final SalaryDao salaryMapper;

	@Autowired
	private AttendanceService attendanceService;

	@Autowired
	private SalaryDao salaryDao;

	public SalaryServiceImpl(SalaryDao salaryMapper) {
		this.salaryMapper = salaryMapper;
	}

	@Override
	public List<SalaryDto> getAllSalaries() {
		return salaryMapper.getAllSalaries();
	}

	@Override
	public boolean addSalary(SalaryDto salaryDto) {
		return salaryMapper.addSalary(salaryDto) > 0;
	}

	@Override
	public boolean updateSalary(SalaryDto salaryDto) {
		return salaryMapper.updateSalary(salaryDto) > 0;
	}

	@Override
	public boolean deleteSalary(Integer salaryId) {
		return salaryMapper.deleteSalary(salaryId) > 0;
	}

	@Override
	public void calculateSalaries(Integer employeeId, String yearMonth) {
		// 근태 데이터 조회
		List<AttendanceDto> attendances = attendanceService.getEmployeeMonthlyAttendance(employeeId, yearMonth);

		if (attendances == null || attendances.isEmpty()) {
			throw new IllegalStateException("근태 데이터가 없습니다.");
		}

		// 급여 계산
		SalaryDto salary = calculateIndividualSalary(attendances.get(0));

		// 필수 필드 설정
		salary.setEmployeeId(employeeId);
		salary.setStatus("CONFIRMED");

		// 기존 급여 정보 확인
		SalaryDto existingSalary = salaryDao.getEmployeeSalaryByMonth(employeeId, yearMonth);

		if (existingSalary != null) {
			// 기존 급여 정보 업데이트 - PaymentDate는 그대로 유지
			salary.setSalaryId(existingSalary.getSalaryId());
			salary.setPaymentDate(existingSalary.getPaymentDate()); // 기존 날짜 유지
			salaryDao.updateSalary(salary);
		} else {
			// 새로운 급여 정보 추가 - 해당 월의 날짜로 설정
			salary.setPaymentDate(LocalDate.parse(yearMonth + "-01")); // 해당 월의 첫째 날로 설정
			salaryDao.addSalary(salary);
		}
	}

	@Override
	public List<SalaryDto> getSalariesByEmployeeId(Integer employeeId) {
		return salaryMapper.getSalariesByEmployeeId(employeeId);
	}

	@Override
	public SalaryDto getSalaryById(Integer salaryId) {
		return salaryMapper.getSalaryById(salaryId);
	}

	private SalaryDto calculateIndividualSalary(AttendanceDto attendance) {
		SalaryDto salary = new SalaryDto();
		salary.setEmployeeId(attendance.getEmployeeId());

		// 기본급과 수당 설정
		BigDecimal baseSalary = new BigDecimal("2800000.00");
		BigDecimal mealAllowance = BigDecimal.valueOf(AllowancePolicy.getMealAllowance());
		BigDecimal positionAllowance = BigDecimal
				.valueOf(AllowancePolicy.getPositionAllowance(attendance.getPosition()));

		// 초과근무수당 계산
		BigDecimal overtimePay = BigDecimal.ZERO;
		double overtimeHours = attendance.getOvertimeHours();

		if (overtimeHours >= 10) {
			int overtimeBonusCount = (int) (overtimeHours / 10); // 10시간 단위로 계산
			overtimePay = new BigDecimal(overtimeBonusCount).multiply(new BigDecimal("100000.00"));
		}

		// 총급여 계산
		BigDecimal totalSalary = baseSalary.add(mealAllowance).add(positionAllowance).add(overtimePay);

		// 공제 계산
		BigDecimal nationalPension = totalSalary.multiply(new BigDecimal("0.045"));
		BigDecimal healthInsurance = totalSalary.multiply(new BigDecimal("0.03545"));
		BigDecimal employmentInsurance = totalSalary.multiply(new BigDecimal("0.009"));
		BigDecimal longTermCareInsurance = healthInsurance.multiply(new BigDecimal("0.1281"));
		BigDecimal incomeTax = totalSalary.multiply(new BigDecimal("0.03"));
		BigDecimal localIncomeTax = incomeTax.multiply(new BigDecimal("0.1"));

		// 실수령액 계산
		BigDecimal totalDeduction = nationalPension.add(healthInsurance).add(employmentInsurance)
				.add(longTermCareInsurance).add(incomeTax).add(localIncomeTax);
		BigDecimal netPay = totalSalary.subtract(totalDeduction);

		// SalaryDto에 데이터 설정
		salary.setBaseSalary(baseSalary);
		salary.setMealAllowance(mealAllowance);
		salary.setPositionAllowance(positionAllowance);
		salary.setOvertimePay(overtimePay);
		salary.setTotalSalary(totalSalary);
		salary.setNationalPension(nationalPension);
		salary.setHealthInsurance(healthInsurance);
		salary.setEmploymentInsurance(employmentInsurance);
		salary.setLongTermCareInsurance(longTermCareInsurance);
		salary.setIncomeTax(incomeTax);
		salary.setLocalIncomeTax(localIncomeTax);
		salary.setDeductionTotal(totalDeduction);
		salary.setNetPay(netPay);

		return salary;
	}
	
	// 이름 or 직급 or 부서로 사원 검색하기
	@Override
	public List<SalaryDto> searchSalaries(String searchType, String keyword) {
		switch (searchType) {
		case "employeeId":
			try {
				int employeeId = Integer.parseInt(keyword);
				return salaryMapper.getSalariesByEmployeeId(employeeId);
			} catch (NumberFormatException e) {
				return new ArrayList<>();
			}
		case "name":
			return salaryMapper.getSalariesByEmployeeName(keyword);
		case "position":
			return salaryMapper.getSalariesByPosition(keyword);
		case "department":
			return salaryMapper.getSalariesByDepartment(keyword);
		default:
			return new ArrayList<>();
		}
	}

	// 급여 확정 상태로 업데이트
	@Override
	public void confirmSalaries(String employeeId, String yearMonth) {
		try {
			// 날짜 형식 변환 (2025-01 형식으로 통일)
			yearMonth = yearMonth.replace("/", "-");

			// 급여 상태 CONFIRMED로 업데이트
			salaryMapper.confirmSalaries(Map.of("employeeId", employeeId, "yearMonth", yearMonth));
		} catch (Exception e) {
			throw new RuntimeException("급여 확정 처리 중 오류가 발생했습니다: " + e.getMessage());
		}
	}

	@Override
	public SalaryDto getEmployeeSalaryByMonth(Integer employeeId, String yearMonth) {
		return salaryMapper.getEmployeeSalaryByMonth(employeeId, yearMonth);
	}

	@Override
	public boolean isSalaryCalculated(String yearMonth) {
		return salaryMapper.isSalaryCalculated(yearMonth);
	}

	@Override
	public List<SalaryDto> getCalculatedSalaries(String yearMonth) {
		return salaryMapper.getCalculatedSalaries(yearMonth);
	}

	@Override
	public boolean isSalaryClosed(Integer employeeId, String yearMonth) {
		SalaryDto salary = salaryMapper.getEmployeeSalaryByMonth(employeeId, yearMonth);
		return salary != null && "CONFIRMED".equals(salary.getStatus());
	}

}