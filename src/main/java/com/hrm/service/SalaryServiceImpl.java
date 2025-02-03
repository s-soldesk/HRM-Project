package com.hrm.service;

import com.hrm.dao.SalaryDao;
import com.hrm.dto.AttendanceDto;
import com.hrm.dto.SalaryDto;
import com.hrm.utils.AllowancePolicy;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
	public void calculateSalaries(String yearMonth) {
		// 근태 마감 확인
		if (!attendanceService.isAttendanceClosed(yearMonth)) {
			throw new IllegalStateException("근태가 마감되지 않았습니다.");
		}

		// 근태 데이터 조회
		List<AttendanceDto> attendances = attendanceService.getMonthlyAttendance(yearMonth);

		// 각 직원별 급여 계산 및 저장
		for (AttendanceDto attendance : attendances) {
			SalaryDto salary = calculateIndividualSalary(attendance);
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
		salaryMapper.confirmSalaries(Map.of("employeeId", employeeId, "yearMonth", yearMonth));
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

}