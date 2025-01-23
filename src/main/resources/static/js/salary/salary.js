// salary.js
document.addEventListener('DOMContentLoaded', function() {
    // 급여 자동 계산 함수
    function calculateSalary() {
        const baseSalary = parseFloat(document.getElementById('baseSalary').value) || 0;
        const positionAllowance = parseFloat(document.getElementById('positionAllowance').value) || 0;
        const mealAllowance = parseFloat(document.getElementById('mealAllowance').value) || 0;
        const overtimePay = parseFloat(document.getElementById('overtimePay').value) || 0;

        // 국민연금 (4.5%)
        const nationalPension = baseSalary * 0.045;
		
        // 건강보험 (3.43%)
        const healthInsurance = baseSalary * 0.0343;
		
        // 고용보험 (0.8%)
        const employmentInsurance = baseSalary * 0.008;
		
        // 장기요양보험 (건강보험의 11.52%)
        const longTermCare = healthInsurance * 0.1152;

        // 공제액 합계
        const totalDeduction = nationalPension + healthInsurance + employmentInsurance + longTermCare;
        
        // 지급액 합계
        const totalPayment = baseSalary + positionAllowance + mealAllowance + overtimePay;
        
        // 실수령액
        const totalSalary = totalPayment - totalDeduction;

        // 계산 결과 표시
        document.getElementById('totalSalary').value = totalSalary.toFixed(0);
        document.getElementById('nationalPension').value = nationalPension.toFixed(0);
        document.getElementById('healthInsurance').value = healthInsurance.toFixed(0);
        document.getElementById('employmentInsurance').value = employmentInsurance.toFixed(0);
        document.getElementById('longTermCare').value = longTermCare.toFixed(0);
    }

    // 입력 필드에 이벤트 리스너 추가
    const inputFields = document.querySelectorAll('.salary-input');
    inputFields.forEach(field => {
        field.addEventListener('input', calculateSalary);
    });
});