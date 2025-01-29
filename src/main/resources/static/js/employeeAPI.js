const api = {

	// 사원 관련 API
	async getEmployees() {
		const response = await fetch('/employees/list');
		return response.json();
	},

	async getEmployee(id) {
		const response = await fetch(`/employees/${id}`);
		return response.json();
	},

	async addEmployee(data) {
		const response = await fetch('/employees/add', {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify(data)
		});
		return response.json();
	},


	// 부서 관련 API
	async getDepartments() {
		const response = await fetch('/employees/department/list');
		return response.json();
	}
};

export default api;