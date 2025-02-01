import api from './employeeAPI.js';
import { validateEmployee } from './employeeUtils.js';

// 모달 관련 함수들

// 추가 모달
let modalElement;
// 추가 모달 초기화
export function modal() {
	modalElement = document.getElementById('addEmployeeModal');
	const addBtn = document.getElementById('addEmployeeBtn');
	const closeBtn = modalElement.querySelector('.close');
	const addEmployeeForm = document.getElementById('addEmployeeForm');
	const cancelBtn = addEmployeeForm.querySelector('button[type="button"]');

	addBtn.onclick = () => {
		modalElement.style.display = 'block';
		loadDepartments();
	};

	closeBtn.onclick = () => modalElement.style.display = 'none';
	cancelBtn.onclick = () => modalElement.style.display = 'none';

	// 폼 제출 이벤트 핸들러 추가
	addEmployeeForm.onsubmit = handleFormSubmit;
}

// 수정 모달
let editModalElement;

// 수정 모달 생성
function createEditModal() {
	const addModal = document.getElementById('addEmployeeModal');
	editModalElement = addModal.cloneNode(true);
	editModalElement.id = 'editEmployeeModal';

	// 타이틀 변경
	const title = editModalElement.querySelector('h2');
	title.textContent = '사원 정보 수정';

	// 폼 ID 변경
	const form = editModalElement.querySelector('form');
	form.id = 'editEmployeeForm';

	// 버튼 텍스트 변경
	const submitButton = form.querySelector('button[type="submit"]');
	submitButton.textContent = '수정';

	document.body.appendChild(editModalElement);
	return editModalElement;
}
// 수정 모달 초기화
function initializeEditModal() {
	const editModal = createEditModal();
	const closeBtn = editModal.querySelector('.close');
	const form = editModal.querySelector('#editEmployeeForm');
	const cancelBtn = form.querySelector('button[type="button"]');

	closeBtn.onclick = () => editModal.style.display = 'none';
	cancelBtn.onclick = () => editModal.style.display = 'none';

	form.onsubmit = handleEditFormSubmit;
}

// 모달 외부 클릭 이벤트 처리
function initializeModalOutsideClick() {
	window.onclick = (event) => {
		if (event.target === modalElement) {
			modalElement.style.display = 'none';
		}
		if (event.target === editModalElement) {
			editModalElement.style.display = 'none';
		}
	};
}


// 수정 폼 데이터 설정
function setEditFormData(employee) {
	const form = document.getElementById('editEmployeeForm');

	form.querySelector('#name').value = employee.name;
	form.querySelector('#dateOfBirth').value = employee.dateOfBirth;
	// 성별 라디오 버튼 설정
	const genderRadio = form.querySelector(`input[name="gender"][value="${employee.gender}"]`);
	if (genderRadio) {
		genderRadio.checked = true;
	}
	form.querySelector('#departmentId').value = employee.departmentId;
	form.querySelector('#position').value = employee.position;
	form.querySelector('#hiredate').value = employee.hiredate;
	form.querySelector('#status').value = employee.status;
	form.querySelector('#phonenumber').value = employee.phonenumber;
	form.querySelector('#email').value = employee.email;

	const existingIdInput = form.querySelector('input[name="employeeId"]');
	if (existingIdInput) {
		existingIdInput.value = employee.employeeId;
	} else {
		const idInput = document.createElement('input');
		idInput.type = 'hidden';
		idInput.name = 'employeeId';
		idInput.value = employee.employeeId;
		form.appendChild(idInput);
	}
}

// 수정 폼 제출 처리
async function handleEditFormSubmit(e) {
	e.preventDefault();

	const formData = new FormData(e.target);
	const data = Object.fromEntries(formData.entries());
	const employeeId = data.employeeId;

	// employeeId를 data 객체에서 제거
	delete data.employeeId;

	if (!validateEmployee(data)) {
		alert("필수 정보를 모두 입력하세요.");
		return;
	}

	try {
		await api.updateEmployee(employeeId, data);
		alert('사원 정보가 수정되었습니다.');
		editModalElement.style.display = 'none';
		employeeList();
		employeeDetail(employeeId);
	} catch (error) {
		console.error('사원 정보 수정 중 오류:', error);
		alert('사원 정보 수정 중 오류가 발생했습니다.');
	}
}

// 수정 버튼 클릭 핸들러
async function handleEditButtonClick(employeeId) {
	editModalElement.style.display = 'block';
	await loadDepartments();

	try {
		const employee = await api.getEmployee(employeeId);
		setEditFormData(employee);
	} catch (error) {
		console.error('직원 정보 조회 중 오류:', error);
		alert('직원 정보를 불러오는데 실패했습니다.');
	}
}

// 부서 목록 로딩
async function loadDepartments() {
	try {
		const departments = await api.getDepartments();
		// 추가 모달의 부서 select 업데이트
		const addDepartmentSelect = modalElement.querySelector('#departmentId');
		addDepartmentSelect.innerHTML = ''; // 기존 옵션 초기화

		// 수정 모달의 부서 select 업데이트
		const editDepartmentSelect = editModalElement.querySelector('#departmentId');
		editDepartmentSelect.innerHTML = ''; // 기존 옵션 초기화

		departments.forEach(dept => {
			// 추가 모달용 option
			const addOption = document.createElement('option');
			addOption.value = dept.departmentId;
			addOption.textContent = dept.departmentname;
			addDepartmentSelect.appendChild(addOption);

			// 수정 모달용 option
			const editOption = document.createElement('option');
			editOption.value = dept.departmentId;
			editOption.textContent = dept.departmentname;
			editDepartmentSelect.appendChild(editOption);
		});
	} catch (error) {
		console.error('부서 목록 로딩 중 오류:', error);
		alert('부서 정보를 불러오는데 실패했습니다.');
	}
}

// 추가 폼 제출 처리
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
		e.target.reset();
		employeeList();
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
export async function employeeList(page) {
	try {
		const data = await api.getEmployees(page);
		displayEmployees(data.employees);
		displayPagination(data.page, data.totalPages);

	} catch (error) {
		console.error('사원 리스트 조회 중 오류:', error);
		alert('사원 목록을 불러오는데 실패했습니다.');
	}
}

function displayEmployees(employees) {
	const tbody = document.getElementById('employeeTableBody');
	tbody.innerHTML = '';

	// 데이터가 없을 경우 처리
	if (!employees || employees.length === 0) {
		const tr = document.createElement('tr');
		const td = document.createElement('td');
		td.colSpan = 3;  // 3개 컬럼을 합침
		td.textContent = '데이터가 없습니다.';
		td.style.textAlign = 'center';
		tr.appendChild(td);
		tbody.appendChild(tr);
		return;
	}

	employees.forEach(emp => {
		const tr = createEmployeeRow(emp);
		tbody.appendChild(tr);
	});
}
// 페이지네이션 UI 표시
function displayPagination(currentPage, totalPages) {
	// undefined 체크 추가
	currentPage = currentPage || 1;
	totalPages = totalPages || 1;

	// 페이지가 없을 경우 처리
	if (totalPages === 0) {
		pagination.innerHTML = '데이터가 없습니다.';
		return;
	}
	const pagination = document.getElementById('pagination');
	pagination.innerHTML = '';

	// 이전 페이지 버튼
	if (currentPage > 1) {
		const prevBtn = document.createElement('button');
		prevBtn.textContent = '이전';
		prevBtn.onclick = () => employeeList(currentPage - 1);
		pagination.appendChild(prevBtn);
	}

	// 페이지 번호들
	for (let i = 1; i <= totalPages; i++) {
		const pageBtn = document.createElement('button');
		pageBtn.textContent = i;
		pageBtn.onclick = () => employeeList(i);

		if (i === currentPage) {
			pageBtn.classList.add('active'); // 현재 페이지 강조
		}

		pagination.appendChild(pageBtn);
	}

	// 다음 페이지 버튼
	if (currentPage < totalPages) {
		const nextBtn = document.createElement('button');
		nextBtn.textContent = '다음';
		nextBtn.onclick = () => employeeList(currentPage + 1);
		pagination.appendChild(nextBtn);
	}
}

// 사원 상세정보 표시
// 사원 아이디를 저장하기 위한  전역 변수
let selectedEmployeeId = null;

export async function employeeDetail(id) {
	try {
		const employee = await api.getEmployee(id);
		selectedEmployeeId = id;
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
	document.getElementById('empDob').textContent = formatDate(employee.dateOfBirth) ?? '';
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

// 검색 폼 설정
function setupSearchForm() {
	const searchForm = document.getElementById('searchForm');

	if (searchForm) {
		searchForm.onsubmit = handleSearch;
	}
}

// 검색 처리
async function handleSearch(e) {
	e.preventDefault();

	const searchType = document.getElementById('searchType').value;
	const keyword = document.getElementById('searchKeyword').value;

	if (!keyword.trim()) {
		alert('검색어를 입력해주세요.');
		return;
	}

	try {
		// ID 검색 시 숫자 검증
		if (searchType === 'id' && !/^\d+$/.test(keyword)) {
			alert('사원 ID는 숫자만 입력 가능합니다.');
			return;
		}

		const employees = await api.searchEmployee(searchType, keyword);

		if (employees.length === 0) {
			alert('검색 결과가 없습니다.');
			return;
		}

		// 검색 결과로 테이블 업데이트
		displayEmployees(employees);

	} catch (error) {
		console.error('검색 중 오류:', error);
		if (error.message.includes('숫자로')) {
			alert('사원 ID는 숫자만 입력 가능합니다.');
		} else {
			alert('검색 중 오류가 발생했습니다.');
		}
	}
}

// 초기화 함수 (외부에서 호출할 수 있도록 export)
export function initializeUI() {
	modal();
	initializeEditModal();
	initializeModalOutsideClick();
	employeeList();
	setupSearchForm();

	const editBtn = document.getElementById('editEmployeeBtn');
	editBtn.addEventListener('click', () => {
		if (selectedEmployeeId) {
			handleEditButtonClick(selectedEmployeeId);
		} else {
			alert('수정할 사원을 선택해주세요.');
		}
	});
}