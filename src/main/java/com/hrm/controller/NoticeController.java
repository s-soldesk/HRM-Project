package com.hrm.controller;

import java.io.File;
import java.io.FileInputStream;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hrm.dto.NoticeDto;
import com.hrm.dto.UserAccountDto;
import com.hrm.mapper.UserAccountsMapper;
import com.hrm.service.NoticeService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class NoticeController {
    private final NoticeService noticeService;
    private final UserAccountsMapper userAccountsMapper;
    
    // 기본 관리자 ID 설정 (로그인 없이 사용하기 위함)
    private static final Integer DEFAULT_ADMIN_ID = 1;
    
    // Admin 권한 체크 단순화
    private boolean isAdmin() {
        return true;  // 테스트를 위해 항상 true 반환
    }
    
    @GetMapping("/notices")
    public String listNotices(Model model, HttpSession session) {
        try {
            // 테스트용 Admin 권한 설정
            session.setAttribute("userRole", "Admin");
            
            List<NoticeDto> notices = noticeService.getAllNotices();
            model.addAttribute("notices", notices);
            
            return "notice/list";
        } catch (Exception e) {
            log.error("Error in listNotices: ", e);
            return "error";
        }
    }
    
    @GetMapping("/notices/{id}")
    public String viewNotice(@PathVariable("id") int noticeId, Model model) {
        try {
            NoticeDto notice = noticeService.getNoticeWithIncreasedReadCount(noticeId);
            if (notice != null) {
                model.addAttribute("notice", notice);
                model.addAttribute("userRole", "Admin"); // 테스트용으로 Admin 권한 부여
                return "notice/view";
            }
            return "redirect:/notices";
        } catch (Exception e) {
            log.error("Error in viewNotice: ", e);
            return "error";
        }
    }
    
    @GetMapping("/notices/new")
    public String newNoticeForm() {
        try {
            return "notice/form";
        } catch (Exception e) {
            log.error("Error in newNoticeForm: ", e);
            return "error";
        }
    }
    
    @PostMapping("/notices")
    public String createNotice(@ModelAttribute NoticeDto notice) {
        try {
            notice.setCreatedDate(LocalDateTime.now());
            notice.setAuthorId(DEFAULT_ADMIN_ID); // 기본 관리자 ID 사용
            noticeService.createNotice(notice);
            log.info("Notice created successfully");
            return "redirect:/notices";
        } catch (Exception e) {
            log.error("Error in createNotice: ", e);
            return "error";
        }
    }
    
    @GetMapping("/notices/{id}/edit")
    public String editNoticeForm(@PathVariable("id") int noticeId, Model model) {
        try {
            NoticeDto notice = noticeService.getNoticeById(noticeId);
            if (notice == null) {
                return "redirect:/notices";
            }
            model.addAttribute("notice", notice);
            return "notice/form";
        } catch (Exception e) {
            log.error("Error in editNoticeForm: ", e);
            return "error";
        }
    }
    
    @PostMapping("/notices/{id}")
    public String updateNotice(@PathVariable("id") int noticeId, 
                             @ModelAttribute NoticeDto notice) {
        try {
            notice.setNoticeId(noticeId);
            noticeService.updateNotice(notice);
            log.info("Notice {} updated successfully", noticeId);
            return "redirect:/notices/" + noticeId;
        } catch (Exception e) {
            log.error("Error in updateNotice: ", e);
            return "redirect:/notices/" + noticeId + "?error";
        }
    }
    
    @PostMapping("/notices/{id}/delete")
    public String deleteNotice(@PathVariable("id") int noticeId) {
        try {
            noticeService.deleteNotice(noticeId);
            log.info("Notice {} deleted successfully", noticeId);
            return "redirect:/notices";
        } catch (Exception e) {
            log.error("Error in deleteNotice: ", e);
            return "redirect:/notices?error";
        }
    }
    
    @GetMapping("/notices/search")
    public String searchNotices(
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "keyword", required = false) String keyword, 
            Model model) {
        try {
            List<NoticeDto> notices;
            if (keyword != null && !keyword.trim().isEmpty()) {
                notices = noticeService.searchNotices(searchType, keyword);
            } else {
                notices = noticeService.getAllNotices();
            }
            
            model.addAttribute("notices", notices);
            model.addAttribute("userRole", "Admin"); // 테스트용으로 Admin 권한 부여
            
            return "notice/list";
        } catch (Exception e) {
            log.error("Error in searchNotices: ", e);
            return "error";
        }
    }
}