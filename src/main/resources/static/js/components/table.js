import api from '../api/employeeAPI.js';
import { formatDate, formatGender, formatStatus } from '../utils/employeeUtils.js';

// 콜백 함수들
let onRowClick;
let onPageChange;

// 콜백 설정 함수들
export function setRowClickCallback(callback) {
	onRowClick = callback;
}

export function setPageChangeCallback(callback) {
	onPageChange = callback;
}

// 사원 행 생성 함수
function createEmployeeRow(emp) {
	const tr = document.createElement('tr');
	tr.setAttribute('data-id', emp.employeeId);

	const tdId = document.createElement('td');
	tdId.textContent = emp.employeeId;

	const tdName = document.createElement('td');
	tdName.textContent = emp.name;

	const tdDept = document.createElement('td');
	tdDept.textContent = emp.department?.departmentname ?? '(부서정보없음)';

	tr.append(tdId, tdName, tdDept);

	tr.addEventListener('click', () => {
		if (onRowClick) {
			onRowClick(emp.employeeId);
		}
	});

	return tr;
}

// 사원 목록 표시
export function displayEmployees(employees) {
	const tbody = document.getElementById('employeeTableBody');
	tbody.innerHTML = '';

	if (!employees || employees.length === 0) {
		const tr = document.createElement('tr');
		const td = document.createElement('td');
		td.colSpan = 3;
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
// 페이지네이션 UI 표시
export function displayPagination(currentPage, totalPages) {
	currentPage = Number(currentPage) || 1;
	totalPages = Number(totalPages) || 1;

	const pagination = document.getElementById('pagination');
	pagination.innerHTML = '';

	if (totalPages === 0) {
		pagination.innerHTML = '데이터가 없습니다.';
		return;
	}

	// 이전 페이지 버튼
	const prevBtn = document.createElement('button');
	prevBtn.textContent = '이전';
	prevBtn.onclick = () => {
		if (currentPage > 1) {
			onPageChange(currentPage - 1);
		}
	};
	prevBtn.disabled = currentPage === 1;
	pagination.appendChild(prevBtn);

	// 페이지 번호 표시 (최대 5개만 표시)
	const startPage = Math.max(1, currentPage - 2);
	const endPage = Math.min(totalPages, startPage + 4);

	for (let i = startPage; i <= endPage; i++) {
		const pageBtn = document.createElement('button');
		pageBtn.textContent = i;
		pageBtn.onclick = () => {
			if (i !== currentPage) {
				onPageChange(i);
			}
		};

		if (i === currentPage) {
			pageBtn.classList.add('active');
		}

		pagination.appendChild(pageBtn);
	}

	// 다음 페이지 버튼
	const nextBtn = document.createElement('button');
	nextBtn.textContent = '다음';
	nextBtn.onclick = () => {
		if (currentPage < totalPages) {
			onPageChange(currentPage + 1);
		}
	};
	nextBtn.disabled = currentPage === totalPages;
	pagination.appendChild(nextBtn);
}

// 상세 정보 업데이트 함수
export function updateDetailView(employee) {
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

// 검색 관련 상태 및 콜백
let onSearch;

export function setSearchCallback(callback) {
	onSearch = callback;
}

// 검색 폼 설정
export function setupSearchForm() {
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
		if (searchType === 'id' && !/^\d+$/.test(keyword)) {
			alert('사원 ID는 숫자만 입력 가능합니다.');
			return;
		}

		// 첫 페이지(1)로 검색 시작
		const searchData = await api.searchEmployee(searchType, keyword, 1);

		if (searchData.employees.length === 0) {
			alert('검색 결과가 없습니다.');
			return;
		}

		if (onSearch) {
			onSearch(searchData); // 페이징 정보를 포함한 전체 데이터 전달
		} else {
			displayEmployees(searchData.employees);
			displayPagination(searchData.page, searchData.totalPages);
		}

	} catch (error) {
		console.error('검색 중 오류:', error);
		if (error.message.includes('숫자로')) {
			alert('사원 ID는 숫자만 입력 가능합니다.');
		} else {
			alert('검색 중 오류가 발생했습니다.');
		}
	}
}