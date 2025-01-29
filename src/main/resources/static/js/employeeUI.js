import api from './employeeAPI.js';
import { validateEmployee } from './employeeUtils.js';

// 모달 관련 함수들
let modalElement;
export function modal() {
	modalElement = document.getElementById('addEmployeeModal');
	const addBtn = document.getElementById('addEmployeeBtn');
	const closeBtn = document.querySelector('.close');
	const addEmployeeForm = document.getElementById('addEmployeeForm');

	addBtn.onclick = () => {
		modalElement.style.display = 'block';
		loadDepartments();
	};

	closeBtn.onclick = () => modalElement.style.display = 'none';

	window.onclick = (event) => {
		if (event.target == modalElement) {
			modalElement.style.display = 'none';
		}
	};

	// 폼 제출 이벤트 핸들러 추가
	addEmployeeForm.onsubmit = handleFormSubmit;
}

// 부서 목록 로딩
async function loadDepartments() {
	try {
		const departments = await api.getDepartments();
		const departmentSelect = document.getElementById('departmentId');
		departmentSelect.innerHTML = ''; // 기존 옵션 초기화

		departments.forEach(dept => {
			const option = document.createElement('option');
			option.value = dept.departmentId;
			option.textContent = dept.departmentname;
			departmentSelect.appendChild(option);
		});
	} catch (error) {
		console.error('부서 목록 로딩 중 오류:', error);
		alert('부서 정보를 불러오는데 실패했습니다.');
	}
}

// 폼 제출 처리 함수
async function handleFormSubmit(e) {
	e.preventDefault();

	const formData = new FormData(e.target);
	const data = Object.fromEntries(formData.entries());

	if (!validateEmployee(data)) {
		alert("필수 정보를 모두 입력하세요.");
		return;
	}

	try {
		await api.addEmployee(data);
		alert('사원이 등록되었습니다.');
		modalElement.style.display = 'none';
		e.target.reset(); // 폼 초기화
		employeeList(); // 목록 새로고침
	} catch (error) {
		console.error('사원 등록 중 오류:', error);
		alert('사원 등록 중 오류가 발생했습니다.');
	}
}

// 사원 행 생성 함수
function createEmployeeRow(emp) {
	const tr = document.createElement('tr');
	tr.setAttribute('data-id', emp.employeeId);

	// 각 컬럼 생성
	const tdId = document.createElement('td');
	tdId.textContent = emp.employeeId;

	const tdName = document.createElement('td');
	tdName.textContent = emp.name;

	const tdDept = document.createElement('td');
	tdDept.textContent = emp.department?.departmentname ?? '(부서정보없음)';

	// 행에 컬럼 추가
	tr.append(tdId, tdName, tdDept);

	// 클릭 이벤트 추가
	tr.addEventListener('click', () => {
		employeeDetail(emp.employeeId);
	});

	return tr;
}

// 사원 목록 표시
export async function employeeList() {
	try {
		const employees = await api.getEmployees();
		displayEmployees(employees);
	} catch (error) {
		console.error('사원 리스트 조회 중 오류:', error);
		alert('사원 목록을 불러오는데 실패했습니다.');
	}
}

function displayEmployees(employees) {
	const tbody = document.getElementById('employeeTableBody');
	tbody.innerHTML = '';

	employees.forEach(emp => {
		const tr = createEmployeeRow(emp);
		tbody.appendChild(tr);
	});
}

// 사원 상세정보 표시
export async function employeeDetail(id) {
	try {
		const employee = await api.getEmployee(id);
		updateDetailView(employee);
	} catch (error) {
		console.error('사원 정보 조회 중 오류:', error);
		alert('사원 상세 정보를 불러오는데 실패했습니다.');
	}
}

// 상세 정보 업데이트 함수
function updateDetailView(employee) {
	// 각 필드 업데이트
	document.getElementById('empName').textContent = employee.name ?? '';
	document.getElementById('empDob').textContent = formatDate(employee.dateBirth) ?? '';
	document.getElementById('empGender').textContent = formatGender(employee.gender) ?? '';
	document.getElementById('empDepartment').textContent = employee.department?.departmentname ?? '(부서정보없음)';
	document.getElementById('empPosition').textContent = employee.position ?? '';
	document.getElementById('empHireDate').textContent = formatDate(employee.hiredate) ?? '';
	document.getElementById('empStatus').textContent = formatStatus(employee.status) ?? '';
	document.getElementById('empPhoneNumber').textContent = employee.phonenumber ?? '';
	document.getElementById('empEmail').textContent = employee.email ?? '';
}

// 유틸리티 함수들
function formatDate(dateString) {
	if (!dateString) return '';
	const date = new Date(dateString);
	return date.toLocaleDateString('ko-KR');
}

function formatGender(gender) {
	const genderMap = {
		'M': '남성',
		'F': '여성'
	};
	return genderMap[gender] || gender;
}

function formatStatus(status) {
	const statusMap = {
		'Active': '재직',
		'Inactive': '휴직',
		'Retirement': '퇴사'
	};
	return statusMap[status] || status;
}

// 초기화 함수 (외부에서 호출할 수 있도록 export)
export function initializeUI() {
	modal();
	employeeList();
}