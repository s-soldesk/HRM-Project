import api from './api/employeeAPI.js';
import { initializeAddModal, initializeEditModal, handleEditButtonClick, initializeModalOutsideClick, setUpdateCallback } from './components/modal.js';
import { displayEmployees, displayPagination, updateDetailView, setupSearchForm, setRowClickCallback, setPageChangeCallback, setSearchCallback } from './components/table.js';

// 사원 아이디를 저장하기 위한 전역 변수
let selectedEmployeeId = null;

// 사원 목록 표시
async function employeeList(page) {
	try {
		const data = await api.getEmployees(page);
		displayEmployees(data.employees);
		displayPagination(data.page, data.totalPages);
	} catch (error) {
		console.error('사원 리스트 조회 중 오류:', error);
		alert('사원 목록을 불러오는데 실패했습니다.');
	}
}

// 사원 상세정보 표시
async function employeeDetail(id) {
	try {
		const employee = await api.getEmployee(id);
		selectedEmployeeId = id;
		updateDetailView(employee);
	} catch (error) {
		console.error('사원 정보 조회 중 오류:', error);
		alert('사원 상세 정보를 불러오는데 실패했습니다.');
	}
}

// 검색 결과 처리
let currentSearchType = null;
let currentKeyword = null;

function handleSearchResults(searchData) {
	displayEmployees(searchData.employees);
	displayPagination(searchData.page, searchData.totalPages);

	// 현재 검색 조건 저장
	currentSearchType = searchData.searchType;
	currentKeyword = searchData.keyword;
}

// 초기화 함수
export function initializeUI() {
	// 모달 초기화
	initializeAddModal();
	initializeEditModal();
	initializeModalOutsideClick();

	// 콜백 함수 설정
	setUpdateCallback((id) => {
		employeeList();
		if (id) {
			employeeDetail(id);
		}
	});

	setRowClickCallback((id) => {
		employeeDetail(id);
	});

	// 페이지 변경 콜백
	setPageChangeCallback(async (page) => {
		if (currentSearchType && currentKeyword) {
			// 검색 상태일 때
			const searchData = await api.searchEmployee(currentSearchType, currentKeyword, page);
			handleSearchResults(searchData);
		} else {
			// 일반 목록 조회일 때
			employeeList(page);
		}
	});

	setSearchCallback(handleSearchResults);

	// 검색 폼 설정
	setupSearchForm();

	// 수정 버튼 이벤트 설정
	const editBtn = document.getElementById('editEmployeeBtn');
	editBtn.addEventListener('click', () => {
		if (selectedEmployeeId) {
			handleEditButtonClick(selectedEmployeeId);
		} else {
			alert('수정할 사원을 선택해주세요.');
		}
	});

	// 초기 사원 목록 로드
	employeeList();
}