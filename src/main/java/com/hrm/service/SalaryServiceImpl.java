package com.hrm.service;

import com.hrm.dao.SalaryDao;
import com.hrm.dto.AttendanceDto;
import com.hrm.dto.SalaryDto;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
	public List<SalaryDto> getSalariesByEmployeeId(Integer employeeId) {
		return salaryMapper.getSalariesByEmployeeId(employeeId);
	}

	@Override
	public SalaryDto getSalaryById(Integer salaryId) {
		return salaryMapper.getSalaryById(salaryId);
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

	@Override
	public void calculateSalaries(String yearMonth) {
		// 근태 마감 확인
		if (!attendanceService.isAttendanceClosed(yearMonth)) {
			throw new IllegalStateException("근태가 마감되지 않았습니다.");
		}

		// 근태 데이터 조회
		List<AttendanceDto> attendances = attendanceService.getMonthlyAttendance(yearMonth);

		// 각 직원별 급여 계산
		for (AttendanceDto attendance : attendances) {
			SalaryDto salary = calculateIndividualSalary(attendance);
			salaryMapper.addSalary(salary);
		}
	}

	private SalaryDto calculateIndividualSalary(AttendanceDto attendance) {
		SalaryDto salary = new SalaryDto();

		// 기본급 설정
		salary.setEmployeeId(attendance.getEmployeeId());
		salary.setBaseSalary(new BigDecimal("3000000")); // 기본급 예시

		// 연장근무수당 계산 (시급 2만원 가정)
		BigDecimal overtimePay = new BigDecimal(attendance.getOvertimeHours()).multiply(new BigDecimal("20000"));
		salary.setOvertimePay(overtimePay);

		// 식대 (고정)
		salary.setMealAllowance(new BigDecimal("100000"));

		// 직책수당 (임시로 고정값 설정)
		salary.setPositionAllowance(new BigDecimal("200000"));

		// 총급여 계산
		BigDecimal totalSalary = salary.getBaseSalary().add(salary.getOvertimePay()).add(salary.getMealAllowance())
				.add(salary.getPositionAllowance());
		salary.setTotalSalary(totalSalary);

		// 공제항목 계산
		calculateDeductions(salary);

		return salary;
	}

	private void calculateDeductions(SalaryDto salary) {
		BigDecimal totalSalary = salary.getTotalSalary();

		// 국민연금 (4.5%)
		salary.setNationalPension(totalSalary.multiply(new BigDecimal("0.045")));

		// 건강보험 (3.545%)
		salary.setHealthInsurance(totalSalary.multiply(new BigDecimal("0.03545")));

		// 고용보험 (0.9%)
		salary.setEmploymentInsurance(totalSalary.multiply(new BigDecimal("0.009")));

		// 장기요양보험 (건강보험료의 12.81%)
		salary.setLongTermCareInsurance(salary.getHealthInsurance().multiply(new BigDecimal("0.1281")));

		// 소득세 (간단한 예시)
		salary.setIncomeTax(totalSalary.multiply(new BigDecimal("0.03")));

		// 지방소득세
		salary.setLocalIncomeTax(salary.getIncomeTax().multiply(new BigDecimal("0.1")));
	}

	@Override
	public void confirmSalaries(String yearMonth) {
		// 급여 확정 상태로 업데이트
		salaryMapper.confirmSalaries(yearMonth);
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
	public SalaryDto getEmployeeSalaryByMonth(Integer employeeId, String yearMonth) {
		// 특정 월의 급여 정보를 DAO를 통해 조회
		return salaryDao.getEmployeeSalaryByMonth(employeeId, yearMonth);
	}

}
