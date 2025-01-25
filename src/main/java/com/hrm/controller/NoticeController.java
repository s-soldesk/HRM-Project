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
import com.hrm.dto.UserAccounts;
import com.hrm.mapper.UserAccountsMapper;
import com.hrm.service.NoticeService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;
    private final UserAccountsMapper userAccountsMapper;
    
    @GetMapping("/notices")
    public String listNotices(Model model) {
        model.addAttribute("notices", noticeService.getAllNotices());
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