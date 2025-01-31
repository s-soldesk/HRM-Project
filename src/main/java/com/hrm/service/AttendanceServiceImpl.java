package com.hrm.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hrm.dao.AttendanceDao;
import com.hrm.dto.AttendanceDto;

@Service
public class AttendanceServiceImpl implements AttendanceService {

   @Autowired
   private AttendanceDao attendanceMapper;

   @Override
   public List<AttendanceDto> getAllEmployeeAttendance(String yearMonth) {
       return attendanceMapper.getAllEmployeeAttendance(yearMonth);
   }

   @Override
   public List<AttendanceDto> getAttendanceByDepartment(String yearMonth, String department) {
       return attendanceMapper.getAttendanceByDepartment(yearMonth, department);
   }

   @Override
   public boolean confirmAttendance(String yearMonth) {
       // 1. 해당 월의 근태 데이터가 모두 있는지 확인
       List<AttendanceDto> attendances = getAllEmployeeAttendance(yearMonth);
       if (attendances == null || attendances.isEmpty()) {
           return false;
       }

       // 2. 모든 근태 상태가 정상인지 확인
       boolean allValid = attendances.stream()
               .allMatch(a -> "OnTime".equals(a.getStatus()) || "Approved".equals(a.getStatus()));
       if (!allValid) {
           return false;
       }

       // 3. 근태 확정 상태로 업데이트
       try {
           attendanceMapper.confirmAttendance(yearMonth);
           return true;
       } catch (Exception e) {
           return false;
       }
   }

   @Override
   public boolean isAttendanceClosed(String yearMonth) {
       return attendanceMapper.isAttendanceClosed(yearMonth);
   }
   
   @Override
   public void closeAttendance(String yearMonth) {
       attendanceMapper.closeAttendance(yearMonth);
   }
   
   @Override
   public List<AttendanceDto> getMonthlyAttendance(String yearMonth) {
       return attendanceMapper.getMonthlyAttendance(yearMonth);
   }
}