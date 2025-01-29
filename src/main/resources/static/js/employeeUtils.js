// 유효성 검사
export function validateEmployee(employee) {
	if (!employee.name) return false
	return true
}