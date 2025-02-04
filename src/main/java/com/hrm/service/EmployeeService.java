package com.hrm.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hrm.dao.EmployeeDao;
import com.hrm.dto.DepartmentDto;
import com.hrm.dto.EmployeeDto;
import com.hrm.mapper.EmployeeMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService {

   private final EmployeeMapper employeeMapper;
   private final EmployeeDao employeeDao;

   // 사원 리스트 (사원번호, 사원이름, 부서이름)
   public List<EmployeeDto> employeesList(int offset, int page) {
      return employeeDao.employeesList(page, offset);
   }

   // 전체 사원 수
   public int totalEmployees() {
      return employeeDao.totalEmployees();
   }

   // 사원 상세정보 (사원번호, 생년월일, 직급 등...)
   public EmployeeDto employeesDetail(int EmployeeID) {
      return employeeDao.employeesDetail(EmployeeID);
   }

   // 부서 리스트
   public List<DepartmentDto> departmentsList() {
      return employeeDao.departmentList();
   }

   // 사원 추가
   public EmployeeDto addEmployee(EmployeeDto employeeDto) {
      // 사원을 추가하고
      int result = employeeDao.addEmployee(employeeDto);

      if (result > 0) {
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
            throw new IllegalArgumentException("사원 ID는 숫자로 검색해야합니다.");
         }
      }
     }
      
      public EmployeeDto getEmployeeById(Integer employeeId) {
          try {
              return employeeMapper.getEmployeeById(employeeId);
          } catch (Exception e) {
              throw new RuntimeException("Failed to get employee with ID: " + employeeId, e);
          }
   }
}