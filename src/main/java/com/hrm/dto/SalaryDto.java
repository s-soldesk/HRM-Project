package com.hrm.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
		BigDecimal total = BigDecimal.ZERO;

		// null 체크를 하면서 각 값을 더함
		if (incomeTax != null)
			total = total.add(incomeTax);
		if (localIncomeTax != null)
			total = total.add(localIncomeTax);
		if (nationalPension != null)
			total = total.add(nationalPension);
		if (healthInsurance != null)
			total = total.add(healthInsurance);
		if (employmentInsurance != null)
			total = total.add(employmentInsurance);
		if (longTermCareInsurance != null)
			total = total.add(longTermCareInsurance);

		return total;
	}

	// 실지급액 계산
	public BigDecimal getNetPay() {
		BigDecimal deductions = getDeductionTotal();
		return totalSalary != null ? totalSalary.subtract(deductions) : BigDecimal.ZERO;
	}
	
	// yearMonth 문자열 반환 메서드 추가
    public String getYearMonth() {
        if (paymentDate != null) {
            return paymentDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        }
        return null;
    }

    // yearMonth 설정 메서드 추가
    public void setYearMonth(String yearMonth) {
        if (yearMonth != null) {
            // yyyy-MM 또는 yyyy/MM 형식 모두 처리
            yearMonth = yearMonth.replace("/", "-");
            this.paymentDate = LocalDate.parse(yearMonth + "-01");
        }
    }

	private EmployeeDto employee; // 사원 정보 매핑

}
