package com.hrm.dao;

import org.apache.ibatis.annotations.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Mapper
public interface AttendanceDao {

    // 오늘 출근 기록이 존재하는지 확인
    @Select("SELECT COUNT(*) > 0 FROM Attendance WHERE EmployeeID = #{employeeId} AND Date = #{date}")
    boolean existsTodayRecord(@Param("employeeId") int employeeId, @Param("date") LocalDate date);

    // 출근 기록 추가
    @Insert("INSERT INTO Attendance (EmployeeID, Date, CheckInTime) VALUES (#{employeeId}, #{date}, #{checkInTime})")
    void insertCheckIn(@Param("employeeId") int employeeId, @Param("date") LocalDate date, @Param("checkInTime") LocalTime checkInTime);

    // 출근 시간 가져오기
    @Select("SELECT CheckInTime FROM Attendance WHERE EmployeeID = #{employeeId} AND Date = #{date}")
    LocalTime getCheckInTime(@Param("employeeId") int employeeId, @Param("date") LocalDate date);

    // 퇴근 시간, 근무 시간, 초과 근무 시간 및 근태 상태 업데이트
    @Update("UPDATE Attendance " +
            "SET CheckOutTime = #{checkOutTime}, " +
            "HoursWorked = #{hoursWorked}, " +
            "OvertimeHours = #{overtimeHours}, " +
            "AttendanceType = #{attendanceType} " +
            "WHERE EmployeeID = #{employeeId} AND Date = #{date}")
    void updateCheckOut(@Param("employeeId") int employeeId,
                        @Param("date") LocalDate date,
                        @Param("checkOutTime") LocalTime checkOutTime,
                        @Param("hoursWorked") double hoursWorked,
                        @Param("overtimeHours") double overtimeHours,
                        @Param("attendanceType") String attendanceType);
}
