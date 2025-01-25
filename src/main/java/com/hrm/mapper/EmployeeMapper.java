package com.hrm.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.hrm.dto.EmployeeDto;

@Mapper
public interface EmployeeMapper {
   @Select("SELECT e.*, d.DepartmentName" +
           " FROM Employee e" +
           " LEFT JOIN Department d ON e.DepartmentID = d.DepartmentID" +
           " WHERE e.Email = #{email}")
   EmployeeDto getEmployeeByEmail(String email);
}