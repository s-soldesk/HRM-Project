const api = {

	// 사원 CRUD API
	async getEmployees(page) {
		// page가 undefined, null, 또는 유효하지 않은 값일 경우 1로 설정
		const validPage = (!page || isNaN(page)) ? 1 : page;
		const response = await fetch(`/employees/list?page=${validPage}`);
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
	async updateEmployee(id, data) {
		const response = await fetch(`/employees/${id}`, {
			method: 'PUT',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify(data)
		});
		return response.json();
	},
	// 사원 검색 API
	async searchEmployee(searchType, keyword) {
		const response = await fetch(`/employees/search?searchType=${searchType}&keyword=${keyword}`);
		return response.json();
	},


	// 부서 관련 API
	async getDepartments() {
		const response = await fetch('/employees/department/list');
		return response.json();
	},
};

export default api;