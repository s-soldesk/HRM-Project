package com.hrm.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.hrm.dto.DepartmentDto;
import com.hrm.dto.EmployeeDto;

@Mapper
public interface EmployeeDao {
	/*
	 * 사원 + 부서 조인 하여 전체사원을 조회
	 * GET /employees/list
	 *  (리스트) Employee 테이블과 Department 테이블을 NATURAL JOIN하여
	 * 사원번호 + 사원 이름 + 부서 이름만 가져옴.
	 * + 페이징 적용
	 */
	@Select({ """
			SELECT
				e.EmployeeID,
				e.Name,
				d.DepartmentName
			FROM Employee e NATURAL JOIN Department d
			ORDER BY EmployeeID
			LIMIT #{pageSize}
			OFFSET #{offset}
			""" })
	@Result(property = "department.departmentname", column = "DepartmentName")
	// "DepartmentName" 컬럼의 값을 EmployeeDto 의 Department 객체의 departmentname 필드에 넣음.
	List<EmployeeDto> employeesList(@Param("pageSize") int pageSize, @Param("offset") int offset);
	@Select("SELECT COUNT(*) FROM Employee")
	int totalEmployees();

	/*
	 *  사원 세부정보 조회
	 *  GET /employees/{employeeID}
	 */
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
	

	/*
	 * 부서 리스트 조회
	 * GET /employees/department/list
	 * 사원을 추가하는 모달에서 사용
	 */
	@Select({
		"SELECT * FROM Department ORDER BY DepartmentID"
	})
	List<DepartmentDto> departmentList();
	
	/*
	 * 사원 추가
	 * POST /employees/add
	 */
	@Insert({ """
			INSERT INTO Employee(
				Name,
				DateOfBirth,
				Gender,
				DepartmentID,
				Position,
				HireDate,
				Status,
				PhoneNumber,
				Email
			)
			VALUES(
				#{name},
	            #{dateOfBirth},
	            #{gender},
	            #{departmentId},
	            #{position},
	            #{hiredate},
	            #{status},
	            #{phonenumber},
	            #{email}
			)
			"""
	})
	// INSERT 후에 생성된 EmployeeID 값을 자동으로 EmployeeDto 객체의 employeeId 필드에 설정
	@Options(useGeneratedKeys = true, keyProperty =  "employeeId")
	int addEmployee(EmployeeDto employeeDto);
	
	/*
	 *  사원 수정
	 *  PUT /employees/{id}
	 */
	@Update({"""
        UPDATE Employee 
        SET
			Name = #{name},
	        DateOfBirth = #{dateOfBirth},
	        Gender = #{gender},
	        DepartmentID = #{departmentId},
	        Position = #{position},
	        HireDate = #{hiredate},
	        Status = #{status},
	        PhoneNumber = #{phonenumber},
	        Email = #{email}
        WHERE EmployeeID = #{employeeId}
        """
	})
	int updateEmployee(EmployeeDto employeeDto);
	
	/*-----------------------------사원 검색--------------------------------- */ 
	 /*
	  *  GET /employees/search
	 */
	// 사원 ID로 검색
	@Select({"""
			SELECT
				e.EmployeeID,
				e.Name,
				d.DepartmentName
			FROM Employee e NATURAL JOIN Department d
			WHERE EmployeeID = #{keyword}
			"""
	})
	@Result(property = "department.departmentname", column = "DepartmentName")
	List<EmployeeDto> searchById(int keyword);
	
	// 사원 이름으로 검색
	@Select({"""
		SELECT
			e.EmployeeID,
			e.Name,
			d.DepartmentName
		FROM Employee e NATURAL JOIN Department d
		WHERE Name LIKE CONCAT('%', #{keyword}, '%')
		ORDER BY EmployeeID
		"""
	})
	@Result(property = "department.departmentname", column = "DepartmentName")
	List<EmployeeDto> searchByName(String keyword);
	
	// 사원의 부서로 검색
	@Select({"""
		SELECT
			e.EmployeeID,
			e.Name,
			d.DepartmentName
		FROM Employee e NATURAL JOIN Department d
		WHERE DepartmentName LIKE CONCAT('%', #{keyword}, '%')
		ORDER BY EmployeeID
		"""
	})
	@Result(property = "department.departmentname", column = "DepartmentName")
	List<EmployeeDto> searchByDept(String keyword);
	/*-----------------------------사원 검색--------------------------------- */ 
}
