package com.hrm.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.hrm.dto.AttendanceDto;

@Mapper
public interface AttendanceDao {
    // 특정 월의 모든 직원 근태 데이터 조회
    List<AttendanceDto> getAllEmployeeAttendance(String yearMonth);
    
    // 특정 월, 특정 부서의 근태 데이터 조회
    List<AttendanceDto> getAttendanceByDepartment(@Param("yearMonth") String yearMonth, 
                                                 @Param("department") String department);
    
    // 특정 직원의 특정 월 근태 데이터 조회
    List<AttendanceDto> getMonthlyAttendance(String yearMonth);
    
    // 근태 확정 처리
    void confirmAttendance(String yearMonth);
    
    // 근태 마감 처리
    void closeAttendance(String yearMonth);
    
    // 근태 마감 여부 확인
    boolean isAttendanceClosed(String yearMonth);
    
    // 근태 데이터 저장
    int insertAttendance(AttendanceDto attendance);
    
    // 근태 데이터 수정
    int updateAttendance(AttendanceDto attendance);
}