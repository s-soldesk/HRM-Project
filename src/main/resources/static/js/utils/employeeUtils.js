// 유효성 검사
export function validateEmployee(employee) {
	if (!employee.name) return false
	return true
}

// 유틸리티 함수들
export function formatDate(dateString) {
	if (!dateString) return '';
	const date = new Date(dateString);
	return date.toLocaleDateString('ko-KR');
}

export function formatGender(gender) {
	const genderMap = {
		'M': '남성',
		'F': '여성'
	};
	return genderMap[gender] || gender;
}

export function formatStatus(status) {
	const statusMap = {
		'Active': '재직',
		'Inactive': '휴직',
		'Retirement': '퇴사'
	};
	return statusMap[status] || status;
}