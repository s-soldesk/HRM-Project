package com.hrm.dao;

import com.hrm.dto.AttendanceDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Mapper
public interface AttendanceDao {

	// 근태 기록 조회
	@Select("""
		    SELECT a.AttendanceID, a.EmployeeID, e.Name AS employeeName, 
		           a.CheckInTime, a.CheckOutTime, a.Status, a.Date
		    FROM Attendance a
		    JOIN Employee e ON a.EmployeeID = e.EmployeeID
		    WHERE (#{employeeId} IS NULL OR a.EmployeeID = #{employeeId})
		    AND (#{name} IS NULL OR e.Name LIKE CONCAT('%', #{name}, '%'))
		    AND (#{startDate} IS NULL OR a.Date >= #{startDate})
		    AND (#{endDate} IS NULL OR a.Date <= #{endDate})
		    AND (#{status} IS NULL OR a.Status = #{status})
		""")
		List<AttendanceDto> searchAttendanceRecords(
		    @Param("employeeId") String employeeId,
		    @Param("name") String name,
		    @Param("startDate") String startDate,
		    @Param("endDate") String endDate,
		    @Param("status") String status
		);
 

	
	// 특정한 근태 기록과 해당 사원 이름 조회
	@Select("SELECT a.*, e.name AS employeeName FROM Attendance a " +
	        "JOIN Employee e ON a.EmployeeID = e.EmployeeID " +
	        "WHERE a.AttendanceID = #{attendanceId}")
	AttendanceDto getAttendanceById(@Param("attendanceId") int attendanceId);
	
    
	// 근태 기록 수정
	@Update("UPDATE Attendance " +
	        "SET CheckInTime = CASE WHEN #{status} != 'Leave' THEN #{checkInTime} ELSE NULL END, " +
	        "CheckOutTime = CASE WHEN #{status} != 'Leave' THEN #{checkOutTime} ELSE NULL END, " +
	        "HoursWorked = CASE WHEN #{status} != 'Leave' THEN #{hoursWorked} ELSE NULL END, " +
	        "OvertimeHours = CASE WHEN #{status} != 'Leave' THEN #{overtimeHours} ELSE NULL END, " +
	        "Status = #{status} " +  // status 컬럼 업데이트
	        "WHERE EmployeeID = #{employeeId} AND Date = #{date}")
	void updateAttendance(@Param("employeeId") int employeeId,
	                      @Param("date") LocalDate date,
	                      @Param("checkInTime") LocalTime checkInTime,
	                      @Param("checkOutTime") LocalTime checkOutTime,
	                      @Param("hoursWorked") Double hoursWorked,
	                      @Param("overtimeHours") Double overtimeHours,
	                      @Param("status") String status);



}