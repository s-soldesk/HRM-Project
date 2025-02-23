package com.hrm.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hrm.dto.EmployeeDto;
import com.hrm.dto.NoticeDto;
import com.hrm.entity.RecruitmentPostEntity;
import com.hrm.service.AttendanceService;
import com.hrm.service.EmployeeService;
import com.hrm.service.NoticeService;
import com.hrm.service.RecruitmentPostService;

@Controller
public class DashboardController {
    
    @Autowired
    private AttendanceService attendanceService;
    
    @Autowired
    private NoticeService noticeService;
    
    @Autowired
    private RecruitmentPostService recruitmentService;
    
    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/")
    public String dashboard(Model model, Authentication authentication) {
        // 현재 로그인한 사용자 정보 가져오기
        String currentUserEmail = authentication.getName();
		
		/*
		 * EmployeeDto employee = employeeService.getEmployeeByEmail(currentUserEmail);
		 * 
		 * model.addAttribute("employee", employee);
		 */

        // 현재 달의 근태 현황
        LocalDate now = LocalDate.now();
        String currentMonth = now.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        
        // 근태 통계
		/*
		 * model.addAttribute("attendanceCount",
		 * attendanceService.getAttendanceById(currentMonth));
		 * model.addAttribute("leaveCount",
		 * attendanceService.getLeaveCount(currentMonth));
		 * model.addAttribute("lateCount",
		 * attendanceService.getLateCount(currentMonth));
		 * model.addAttribute("absentCount",
		 * attendanceService.getAbsentCount(currentMonth));
		 * model.addAttribute("vacationCount",
		 * attendanceService.getVacationCount(currentMonth));
		 * model.addAttribute("sickLeaveCount",
		 * attendanceService.getSickLeaveCount(currentMonth));
		 */

        // 최근 공지사항 (최대 5개)
        List<NoticeDto> recentNotices = noticeService.getAllNotices()
            .stream()
            .limit(5)
            .collect(Collectors.toList());
        model.addAttribute("recentNotices", recentNotices);

        // 최근 채용공고 (최대 5개)
        List<RecruitmentPostEntity> recentRecruitments = recruitmentService.getAllPosts()
            .stream()
            .limit(5)
            .collect(Collectors.toList());
        model.addAttribute("recentRecruitments", recentRecruitments);

        return "index";
    }

	/*
	 * @GetMapping("/api/attendance/today")
	 * 
	 * @ResponseBody public Map<String, Object> getTodayAttendance(Authentication
	 * authentication) { String currentUserEmail = authentication.getName(); return
	 * attendanceService.getTodayAttendance(currentUserEmail); }
	 */
}