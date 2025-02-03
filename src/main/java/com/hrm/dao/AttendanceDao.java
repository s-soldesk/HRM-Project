package com.hrm.dao;

import com.hrm.dto.AttendanceDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface AttendanceDao {

	// 근태 기록 조회
	@Select("<script>" +
	        "SELECT a.*, e.name AS employeeName FROM Attendance a " +
	        "JOIN Employee e ON a.EmployeeID = e.EmployeeID " +
	        "WHERE 1=1 " +
	        "<if test='employeeId != null'>AND a.EmployeeID = #{employeeId}</if> " +
	        "<if test='name != null'>AND e.Name LIKE CONCAT('%', #{name}, '%')</if> " +
	        "<if test='startDate != null and endDate != null'>AND a.Date BETWEEN #{startDate} AND #{endDate}</if> " +
	        "<if test='startDate == null and endDate != null'>AND a.Date &lt;= #{endDate}</if> " +
	        "<if test='startDate != null and endDate == null'>AND a.Date &gt;= #{startDate}</if> " +
	        "<if test='attendanceType != null'>AND a.AttendanceType = #{attendanceType}</if> " +
	        "</script>")
	List<AttendanceDto> searchAttendanceRecords(@Param("employeeId") String employeeId,
	                                             @Param("name") String name,
	                                             @Param("startDate") String startDate,
	                                             @Param("endDate") String endDate,
	                                             @Param("attendanceType") String attendanceType);

	
	// 특정한 근태 기록과 해당 사원 이름 조회
	@Select("SELECT a.*, e.name AS employeeName FROM Attendance a " +
	        "JOIN Employee e ON a.EmployeeID = e.EmployeeID " +
	        "WHERE a.AttendanceID = #{attendanceId}")
	AttendanceDto getAttendanceById(@Param("attendanceId") int attendanceId);
	
    
	// 근태 기록 수정
	@Update("UPDATE Attendance SET CheckInTime = #{checkInTime}, CheckOutTime = #{checkOutTime}, AttendanceType = #{attendanceType}, Remarks = #{remarks} WHERE AttendanceID = #{attendanceId}")
    void updateAttendance(AttendanceDto attendance);

}
