package com.hrm.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hrm.dao.EmployeeDao;
import com.hrm.dao.UserAccountDao;
import com.hrm.dto.DepartmentDto;
import com.hrm.dto.EmployeeDto;
import com.hrm.dto.UserAccountDto;
import com.hrm.enums.Role;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeDao employeeDao;
    private final UserAccountDao userAccountDao;
    private final PasswordEncoder passwordEncoder;

    // 사원 리스트 (사원번호, 사원이름, 부서이름)
    public List<EmployeeDto> employeesList(int offset, int page) {
        return employeeDao.employeesList(page, offset);
    }

    // 전체 사원 수
    public int totalEmployees() {
        return employeeDao.totalEmployees();
    }

    // 사원 상세정보 (사원번호, 생년월일, 직급 등...)
    public EmployeeDto employeesDetail(int employeeId) {
        return employeeDao.employeesDetail(employeeId);
    }

    // 부서 리스트
    public List<DepartmentDto> departmentsList() {
        return employeeDao.departmentList();
    }

    /*
     * 사원 추가.
     * Employee, UserAccounts 두 개의 테이블에 INSERT 해야 하므로 트랜잭션 처리!
     */
    @Transactional
    public EmployeeDto addEmployee(EmployeeDto employeeDto) {
        // 사원을 추가하고
        int result = employeeDao.addEmployee(employeeDto);

        if (result > 0) {
            // UserAccount에 추가하기 위한 UserAccountsDto 생성
            UserAccountDto userAccountDto = new UserAccountDto();
            userAccountDto.setEmployeeId(employeeDto.getEmail());
            userAccountDto.setUsername(employeeDto.getName());
            userAccountDto.setPassword(passwordEncoder.encode("1234")); // 기본 비밀번호 암호화

            // 인사부원은 "HR" 권한 부여
            if (employeeDto.getDepartmentId() != null && employeeDto.getDepartmentId() == 1) { // 인사부의 부서 ID는 1
                userAccountDto.setRole(Role.HR);
            } else {
                userAccountDto.setRole(Role.EMPLOYEE);
            }

            // UserAccount 테이블에 사원 로그인 정보 추가
            userAccountDao.addUserAccount(userAccountDto);

            // 추가된 사원의 정보를 반환
            return employeeDao.employeesDetail(employeeDto.getEmployeeId());
        }
        return null;
    }

    // 사원 수정
    public int updateEmployee(EmployeeDto employeeDto) {
        return employeeDao.updateEmployee(employeeDto);
    }

    // 사원 검색
    public List<EmployeeDto> searchEmployee(String searchType, String keyword, int offset, int limit) {
        validateSearchInput(searchType, keyword);
        return employeeDao.searchEmployees(searchType, keyword, offset, limit);
    }

    // 검색된 사원의 수
    public int totalSearchEmployees(String searchType, String keyword) {
        validateSearchInput(searchType, keyword);
        return employeeDao.countSearchEmployees(searchType, keyword);
    }

    // 검색 입력값 검증
    private void validateSearchInput(String searchType, String keyword) {
        if (searchType.equals("id")) {
            try {
                Integer.valueOf(keyword);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("사원 ID는 숫자로 검색해야 합니다.");
            }
        }
    }
}
