package com.hrm.service;

import com.hrm.dao.LeaveDao;
import com.hrm.dao.UserAccountDao;
import com.hrm.dto.ScheduleDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class LeaveService {

    @Autowired
    private LeaveDao leaveDao;
    
    @Autowired
    private ScheduleService scheduleService;
    
    @Autowired
    private UserAccountDao userAccountDao;

    // 모든 휴가 일정 조회
    public List<ScheduleDto> getAllLeaves() {
        return leaveDao.getAllLeaves();
    }

    // 휴가 일정 추가
    public boolean addLeave(ScheduleDto scheduleDto) {
    	// 같은 기간에 대기 중(PENDING)인 휴가가 있는지 확인
        boolean exists = leaveDao.checkPendingLeave(scheduleDto.getEmployeeId(), scheduleDto.getStartDate(), scheduleDto.getEndDate());
        if (exists) {
            return false; // 중복 신청 방지
        }
        
        scheduleDto.setType("Leave");
        
        boolean isAdded = leaveDao.insertLeave(scheduleDto) > 0;
        return isAdded;
    }
    
    // 휴가 상태 업데이트
    public boolean updateLeaveStatus(int scheduleId, String status) {
        // 휴가 정보 조회
        ScheduleDto leaveSchedule = leaveDao.getLeaveById(scheduleId);

        if (leaveSchedule == null) {
            return false;
        }

        // 기존 상태와 동일하면 업데이트 방지
        if (leaveSchedule.getStatus().equals(status)) {
            return false;
        }

        boolean updated = leaveDao.updateLeaveStatus(scheduleId, status) > 0;

        if (updated) {
            if ("CONFIRMED".equals(status)) {
                // 🔹 기존 일정이 없는 경우에만 일정 추가 (중복 방지)
                if (!scheduleService.existsSchedule(scheduleId)) {
                    leaveSchedule.setType("Leave");
                    leaveSchedule.setAllDay(true);
                    scheduleService.createSchedule(leaveSchedule);
                }
            }
        }
        return updated;
    }


    
    // 휴가 승인 시 상태만 업데이트 (중복 추가 방지)
    public boolean approveLeave(int scheduleId) {
        ScheduleDto leaveSchedule = leaveDao.getLeaveById(scheduleId);

        if (leaveSchedule == null || !"PENDING".equals(leaveSchedule.getStatus())) {
            return false; // 이미 처리된 경우 승인 불가
        }

        boolean updated = leaveDao.updateLeaveStatus(scheduleId, "CONFIRMED") > 0;
        if (updated) {
            // ✅ 일정에 자동 추가
            leaveSchedule.setType("Leave");
            leaveSchedule.setAllDay(true);
            scheduleService.createSchedule(leaveSchedule);
        }
        return updated;
    }

    // 휴가 거절 시 상태만 업데이트 (중복 추가 방지)
    public boolean rejectLeave(int scheduleId) {
        ScheduleDto leaveSchedule = leaveDao.getLeaveById(scheduleId);

        if (leaveSchedule == null || !"PENDING".equals(leaveSchedule.getStatus())) {
            return false; // 이미 처리된 경우 거절 불가
        }

        return leaveDao.updateLeaveStatus(scheduleId, "REJECTED") > 0;
    }


    // 휴가 취소 (사원은 PENDING 상태에서만 가능, 승인된 휴가는 HR/관리자만 가능)
    public boolean deleteLeave(int scheduleId, String role) {
        ScheduleDto leave = leaveDao.getLeaveById(scheduleId);
        
        if ("EMPLOYEE".equals(role) && !"PENDING".equals(leave.getStatus())) {
            return false; // 승인된 휴가는 직원이 취소 불가
        }
        
        boolean deleted = leaveDao.deleteLeave(scheduleId) > 0;
        
        if (deleted && "CONFIRMED".equals(leave.getStatus())) {
            scheduleService.deleteSchedule(scheduleId);
        }
        return deleted;
    }
    
    public List<ScheduleDto> getLeavesByEmployee(String employeeEmail) {
        Integer employeeId = userAccountDao.findEmployeeIdByEmail(employeeEmail);
        return employeeId != null ? leaveDao.getLeavesByEmployee(employeeId) : Collections.emptyList();
    }

}
