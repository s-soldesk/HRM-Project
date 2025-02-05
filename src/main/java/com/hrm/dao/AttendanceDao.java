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
	 @Select("""
	            SELECT a.EmployeeID, e.Name AS employeeName, a.CheckInTime, a.CheckOutTime, a.AttendanceType, a.Remarks, a.Date
	            FROM Attendance a
	            JOIN Employee e ON a.EmployeeID = e.EmployeeID
	            WHERE (#{employeeId} IS NULL OR a.EmployeeID = #{employeeId})
	              AND (#{name} IS NULL OR e.Name LIKE CONCAT('%', #{name}, '%'))
	              AND (#{startDate} IS NULL OR a.Date >= #{startDate})
	              AND (#{endDate} IS NULL OR a.Date <= #{endDate})
	              AND (#{attendanceType} IS NULL OR a.AttendanceType = #{attendanceType})
	            UNION ALL
	            SELECT s.EmployeeID, e.Name AS employeeName, NULL AS CheckInTime, NULL AS CheckOutTime, 'Leave' AS AttendanceType, s.Reason AS Remarks, s.StartDate AS Date
	            FROM Schedule s
	            JOIN Employee e ON s.EmployeeID = e.EmployeeID
	            WHERE (#{employeeId} IS NULL OR s.EmployeeID = #{employeeId})
	              AND (#{name} IS NULL OR e.Name LIKE CONCAT('%', #{name}, '%'))
	              AND (#{startDate} IS NULL OR s.StartDate >= #{startDate})
	              AND (#{endDate} IS NULL OR s.EndDate <= #{endDate})
	        """)
	    List<AttendanceDto> searchAttendanceRecords(
	        @Param("employeeId") String employeeId,
	        @Param("name") String name,
	        @Param("startDate") String startDate,
	        @Param("endDate") String endDate,
	        @Param("attendanceType") String attendanceType
	    );

	
	// 특정한 근태 기록과 해당 사원 이름 조회
	@Select("SELECT a.*, e.name AS employeeName FROM Attendance a " +
	        "JOIN Employee e ON a.EmployeeID = e.EmployeeID " +
	        "WHERE a.AttendanceID = #{attendanceId}")
	AttendanceDto getAttendanceById(@Param("attendanceId") int attendanceId);
	
    
	// 근태 기록 수정
	@Update("UPDATE Attendance SET CheckInTime = #{checkInTime}, CheckOutTime = #{checkOutTime}, AttendanceType = #{attendanceType}, Remarks = #{remarks} WHERE AttendanceID = #{attendanceId}")
    void updateAttendance(AttendanceDto attendance);

}
