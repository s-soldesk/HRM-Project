package com.hrm.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import lombok.extern.slf4j.Slf4j;

import com.hrm.dto.EmployeeDto;
import com.hrm.dto.NoticeDto;
import com.hrm.entity.RecruitmentPostEntity;
import com.hrm.service.AttendanceService;
import com.hrm.service.NoticeService;
import com.hrm.service.ProfileService;
import com.hrm.service.RecruitmentPostService;

@Controller
@Slf4j  // Lombok의 로그 어노테이션 추가
public class DashboardController {
    
    private final AttendanceService attendanceService;
    private final NoticeService noticeService;
    private final RecruitmentPostService recruitmentService;
    private final ProfileService profileService;  // EmployeeService 대신 ProfileService 사용

    @Autowired
    public DashboardController(
            AttendanceService attendanceService,
            NoticeService noticeService,
            RecruitmentPostService recruitmentService,
            ProfileService profileService) {
        this.attendanceService = attendanceService;
        this.noticeService = noticeService;
        this.recruitmentService = recruitmentService;
        this.profileService = profileService;
    }

    @GetMapping({"/", "/index"})
    public String dashboard(Model model, Authentication authentication) {
        try {
            // 1. 현재 로그인한 사용자 정보 가져오기
            if (authentication != null) {
                String currentUserEmail = authentication.getName();
                EmployeeDto employee = profileService.getEmployeeByEmail(currentUserEmail);
                model.addAttribute("employee", employee);
            }

            // 2. 공지사항 조회 및 추가
            List<NoticeDto> notices = noticeService.getAllNotices();
            List<NoticeDto> recentNotices = notices.stream()
                .sorted((a, b) -> b.getCreatedDate().compareTo(a.getCreatedDate()))
                .limit(5)
                .collect(Collectors.toList());
            model.addAttribute("recentNotices", recentNotices);

            // 3. 채용공고 조회 및 추가
            List<RecruitmentPostEntity> posts = recruitmentService.getAllPosts();
            List<RecruitmentPostEntity> recentRecruitments = posts.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(5)
                .collect(Collectors.toList());
            model.addAttribute("recentRecruitments", recentRecruitments);

            return "index";
            
        } catch (Exception e) {
            log.error("Dashboard loading error: ", e);
            model.addAttribute("recentNotices", Collections.emptyList());
            model.addAttribute("recentRecruitments", Collections.emptyList());
            return "index";
        }
    }
}