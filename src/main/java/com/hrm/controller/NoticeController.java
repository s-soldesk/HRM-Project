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

import com.hrm.dto.HrInquiryDto;
import com.hrm.dto.NoticeDto;
import com.hrm.dto.UserAccountDto;
import com.hrm.dto.UserAccounts;
import com.hrm.mapper.UserAccountsMapper;
import com.hrm.service.NoticeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class NoticeController {
    private final NoticeService noticeService;
    private final UserAccountsMapper userAccountsMapper;
    
	/*
	 * @GetMapping("/notices") public String listNotices(Model model) { try {
	 * List<NoticeDto> notices = noticeService.getAllNotices();
	 * 
	 * // 로깅 추가 log.info("공지사항 조회 - 총 {} 개의 공지사항 발견", notices.size());
	 * 
	 * // 데이터가 비어있을 경우 로그 추가 if (notices.isEmpty()) { log.warn("공지사항이 존재하지 않습니다.");
	 * }
	 * 
	 * model.addAttribute("notices", notices); return "notice/list"; } catch
	 * (Exception e) { // 예외 처리 로깅 log.error("공지사항 조회 중 오류 발생", e);
	 * model.addAttribute("errorMessage", "공지사항을 불러오는 중 오류가 발생했습니다."); return
	 * "error"; // 에러 페이지로 리다이렉트 } }
	 */
    
    
	   @GetMapping("/notices")
	   public String listNotices(Model model) {
	       List<NoticeDto> notices = noticeService.getAllNotices();
	       model.addAttribute("notices", notices);
	       return "notice/list";
	   }
    
    @GetMapping("/notices/{id}")
    public String viewNotice(@PathVariable("id") int noticeId, Model model) {
        model.addAttribute("notice", noticeService.getNoticeById(noticeId));
        return "notice/view";
    }
    
    @GetMapping("/notices/new")
    public String newNoticeForm() {
        return "notice/form";
    }
    
    @PostMapping("/notices")
    public String createNotice(@ModelAttribute NoticeDto notice) {
        notice.setCreatedDate(LocalDateTime.now());
        noticeService.createNotice(notice);
        return "redirect:/notices";
    }
    
    @GetMapping("/notices/{id}/edit")
    public String editNoticeForm(@PathVariable("id") int noticeId, Model model) {
        model.addAttribute("notice", noticeService.getNoticeById(noticeId));
        return "notice/form";
    }
    
    @PostMapping("/notices/{id}")
    public String updateNotice(@PathVariable("id") int noticeId, @ModelAttribute NoticeDto notice) {
        notice.setNoticeId(noticeId);
        noticeService.updateNotice(notice);
        return "redirect:/notices";
    }
    
    @PostMapping("/notices/{id}/delete")
    public String deleteNotice(@PathVariable("id") int noticeId) {
        noticeService.deleteNotice(noticeId);
        return "redirect:/notices";
    }
    
    @GetMapping("/notices/search")
    public String searchNotices(
        @RequestParam(value = "searchType", required = false) String searchType,
        @RequestParam(value = "keyword", required = false) String keyword, 
        Model model) {
        List<NoticeDto> notices;
        if (keyword != null && !keyword.trim().isEmpty()) {
            notices = noticeService.searchNotices(searchType, keyword);
        } else {
            notices = noticeService.getAllNotices();
        }
        model.addAttribute("notices", notices);
        return "notice/list";
    }
 }