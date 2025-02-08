package com.hrm.dao;

import com.hrm.dto.ScheduleDto;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface LeaveDao {

    // 모든 휴가 일정 조회
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
    List<ScheduleDto> getAllLeaves();

    // 특정 사원의 휴가 일정 조회
    @Select("SELECT * FROM SCHEDULE WHERE EmployeeID = #{employeeId}")
    List<ScheduleDto> getLeavesByEmployee(@Param("employeeId") int employeeId);

    // 휴가 일정 추가
    @Insert("INSERT INTO SCHEDULE (EmployeeID, Title, LeaveType, Reason, StartDate, EndDate, Status) " +
            "VALUES (#{employeeId}, #{title}, #{leaveType}, #{reason}, #{startDate}, #{endDate}, 'PENDING')")
    @Options(useGeneratedKeys = true, keyProperty = "scheduleId")
    int insertLeave(ScheduleDto scheduleDto);

    // 휴가 상태 업데이트
    @Update("UPDATE SCHEDULE SET Status = #{status} WHERE ScheduleID = #{scheduleId}")
    int updateLeaveStatus(@Param("scheduleId") int scheduleId, @Param("status") String status);

    // 휴가 일정 삭제
    @Delete("DELETE FROM SCHEDULE WHERE ScheduleID = #{scheduleId}")
    int deleteLeave(@Param("scheduleId") int scheduleId);
}
