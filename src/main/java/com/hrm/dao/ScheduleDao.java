package com.hrm.dao;

import com.hrm.dto.ScheduleDto;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ScheduleDao {

    // 모든 일정 조회
	@Select("SELECT s.*, e.Name AS employeeName FROM SCHEDULE s " +
	        "JOIN Employee e ON s.EmployeeID = e.EmployeeID")
	@Results({
	        @Result(property = "employeeId", column = "EmployeeID"),
	        @Result(property = "employeeName", column = "employeeName"),
	        @Result(property = "title", column = "Title"),
	        @Result(property = "leaveType", column = "LeaveType"),
	        @Result(property = "reason", column = "Reason"),
	        @Result(property = "startDate", column = "StartDate"),
	        @Result(property = "endDate", column = "EndDate"),
	        @Result(property = "status", column = "Status"),
	        @Result(property = "requestDate", column = "RequestDate")
	})
	List<ScheduleDto> getAllSchedules();

    // 특정 사원의 일정 조회
    @Select("SELECT * FROM SCHEDULE WHERE EmployeeID = #{employeeId}")
    List<ScheduleDto> getSchedulesByEmployee(@Param("employeeId") int employeeId);

    // 일정 추가
    @Insert("INSERT INTO SCHEDULE (EmployeeID, Title, LeaveType, Reason, StartDate, EndDate, Status) " +
            "VALUES (#{employeeId}, #{title}, #{leaveType}, #{reason}, #{startDate}, #{endDate}, 'PENDING')")
    @Options(useGeneratedKeys = true, keyProperty = "scheduleId")
    int insertSchedule(ScheduleDto scheduleDto);

    // 일정 상태 업데이트 (HR이 승인 또는 거절)
    @Update("UPDATE SCHEDULE SET Status = #{status} WHERE ScheduleID = #{scheduleId}")
    int updateScheduleStatus(@Param("scheduleId") int scheduleId, @Param("status") String status);

    // 일정 삭제
    @Delete("DELETE FROM SCHEDULE WHERE ScheduleID = #{scheduleId}")
    int deleteSchedule(@Param("scheduleId") int scheduleId);
}
