package com.hrm.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SalaryDto {

	private Integer salaryId; // 급여id
	private Integer employeeId; // 사원번호
	private BigDecimal baseSalary; // 기본급여 돈 단위는 BigDecimal이 필수라함.
	private BigDecimal overtimePay; // 초과수당
	private BigDecimal totalSalary; // 총급여
	private BigDecimal incomeTax; // 소득세
	private BigDecimal localIncomeTax; // 지방소득세
	private BigDecimal nationalPension; // 국민연금
	private BigDecimal healthInsurance; // 건강보험
	private BigDecimal employmentInsurance; // 고용보험
	private BigDecimal longTermCareInsurance; // 장기요양보험
	private BigDecimal mealAllowance; // 식대비
	private BigDecimal positionAllowance; // 직책수당
	private LocalDate paymentDate; // 급여지급일
	private BigDecimal deductionTotal; // 공제총액
	private BigDecimal netPay; // 실지급액
	private String status; // 급여 지급 상태

	// 공제총액 계산 메서드
	public BigDecimal getDeductionTotal() {
		return incomeTax.add(localIncomeTax).add(nationalPension).add(healthInsurance).add(employmentInsurance)
				.add(longTermCareInsurance);
	}

	// 실지급액 계산 메서드
	public BigDecimal getNetPay() {
		return totalSalary.subtract(getDeductionTotal());
	}

	private EmployeeDto employee; // 사원 정보 매핑

}
