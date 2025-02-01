import api from '../api/employeeAPI.js';
import { validateEmployee } from '../utils/employeeUtils.js';

// 모달 관련 상태 관리
let modalElement;
let editModalElement;
let onEmployeeUpdate; // 콜백 함수

// 콜백 설정 함수
export function setUpdateCallback(callback) {
	onEmployeeUpdate = callback;
}

// 추가 모달 초기화
export function initializeAddModal() {
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

	addEmployeeForm.onsubmit = handleFormSubmit;
}

// 수정 모달 생성
function createEditModal() {
	const addModal = document.getElementById('addEmployeeModal');
	editModalElement = addModal.cloneNode(true);
	editModalElement.id = 'editEmployeeModal';

	const title = editModalElement.querySelector('h2');
	title.textContent = '사원 정보 수정';

	const form = editModalElement.querySelector('form');
	form.id = 'editEmployeeForm';

	const submitButton = form.querySelector('button[type="submit"]');
	submitButton.textContent = '수정';

	document.body.appendChild(editModalElement);
	return editModalElement;
}

// 수정 모달 초기화
export function initializeEditModal() {
	const editModal = createEditModal();
	const closeBtn = editModal.querySelector('.close');
	const form = editModal.querySelector('#editEmployeeForm');
	const cancelBtn = form.querySelector('button[type="button"]');

	closeBtn.onclick = () => editModal.style.display = 'none';
	cancelBtn.onclick = () => editModal.style.display = 'none';

	form.onsubmit = handleEditFormSubmit;
}

// 모달 외부 클릭 이벤트 처리
export function initializeModalOutsideClick() {
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
		if (onEmployeeUpdate) {
			onEmployeeUpdate();
		}
	} catch (error) {
		console.error('사원 등록 중 오류:', error);
		alert('사원 등록 중 오류가 발생했습니다.');
	}
}

// 수정 폼 제출 처리
async function handleEditFormSubmit(e) {
	e.preventDefault();

	const formData = new FormData(e.target);
	const data = Object.fromEntries(formData.entries());
	const employeeId = data.employeeId;

	delete data.employeeId;

	if (!validateEmployee(data)) {
		alert("필수 정보를 모두 입력하세요.");
		return;
	}

	try {
		await api.updateEmployee(employeeId, data);
		alert('사원 정보가 수정되었습니다.');
		editModalElement.style.display = 'none';
		if (onEmployeeUpdate) {
			onEmployeeUpdate(employeeId);
		}
	} catch (error) {
		console.error('사원 정보 수정 중 오류:', error);
		alert('사원 정보 수정 중 오류가 발생했습니다.');
	}
}

// 수정 버튼 클릭 핸들러
export async function handleEditButtonClick(employeeId) {
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

		const addDepartmentSelect = modalElement.querySelector('#departmentId');
		const editDepartmentSelect = editModalElement.querySelector('#departmentId');

		addDepartmentSelect.innerHTML = '';
		editDepartmentSelect.innerHTML = '';

		departments.forEach(dept => {
			const addOption = document.createElement('option');
			addOption.value = dept.departmentId;
			addOption.textContent = dept.departmentname;
			addDepartmentSelect.appendChild(addOption);

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

// 모달 요소 export
export { modalElement, editModalElement };