package com.hrm.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import com.hrm.dto.EmployeeDto;

@Mapper
public interface EmployeeDao {
	/*
	 * 사원 + 부서 조인 하여 전체사원을 조회 (리스트) Employee 테이블과 Department 테이블을 NATURAL JOIN하여
	 * 사원번호 + 사원 이름 + 부서 이름만 가져옴.
	 */
	@Select({ """
			SELECT
				e.EmployeeID,
				e.Name,
				d.DepartmentName
			FROM Employee e NATURAL JOIN Department d
			""" })
	@Result(property = "department.departmentname", column = "DepartmentName")
	// "DepartmentName" 컬럼의 값을 EmployeeDto 의 Department 객체의 departmentname 필드에 넣음.
	List<EmployeeDto> employeesList();

	// 사원 세부정보 조회
	@Select({ """
				SELECT
					e.EmployeeID,
					e.Name,
					e.DateOfBirth,
					e.Gender,
					e.DepartmentID,
					e.Position,
					e.HireDate,
					e.Status,
					e.PhoneNumber,
					e.Email,

		            d.DepartmentName,
		            d.Location
				FROM Employee e NATURAL JOIN Department d
				WHERE EmployeeID = #{EmployeeID}
			"""
	})
	@Results({
		@Result(property = "department.departmentname", column = "DepartmentName"),
		@Result(property = "department.location", column = "Location"),
	})
	EmployeeDto employeesDetail(int EmployeeID);
}
