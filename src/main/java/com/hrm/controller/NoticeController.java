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

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class NoticeController {
   private final NoticeService noticeService;
   private final UserAccountsMapper userAccountsMapper;
   
   @GetMapping("/notices")
   public String listNotices(Model model, HttpSession session) {
       try {
           // 세션에서 사용자 정보와 역할 가져오기
           UserAccountDto user = (UserAccountDto) session.getAttribute("loggedInUser");
           String userRole = (String) session.getAttribute("userRole");
           
           if (user == null) {
               return "redirect:/login";
           }

           List<NoticeDto> notices = noticeService.getAllNotices();
           
           model.addAttribute("notices", notices);
           model.addAttribute("userRole", userRole);
           log.info("Loaded {} notices for user role: {}", notices.size(), userRole);
           
           return "notice/list";
       } catch (Exception e) {
           log.error("Error in listNotices: ", e);
           return "error";
       }
   }
   
   @GetMapping("/notices/{id}")
   public String viewNotice(@PathVariable("id") int noticeId, Model model, HttpSession session) {
       try {
           // 조회수를 증가시키고 공지사항 정보를 가져옴
           NoticeDto notice = noticeService.getNoticeWithIncreasedReadCount(noticeId);
           
           if (notice != null) {
               model.addAttribute("notice", notice);
               model.addAttribute("userRole", session.getAttribute("userRole"));
               return "notice/view";
           }
           
           return "redirect:/notices";
       } catch (Exception e) {
           log.error("Error in viewNotice: ", e);
           return "error";
       }
   }
   
   @GetMapping("/notices/new")
   public String newNoticeForm(HttpSession session) {
       try {
           UserAccountDto user = (UserAccountDto) session.getAttribute("loggedInUser");
           String userRole = (String) session.getAttribute("userRole");
           
           if (user == null) {
               return "redirect:/login";
           }
           
           // ADMIN만 공지사항 작성 가능
           if (!"ADMIN".equals(userRole)) {
               return "redirect:/notices";
           }
           
           return "notice/form";
       } catch (Exception e) {
           log.error("Error in newNoticeForm: ", e);
           return "error";
       }
   }
   
   @PostMapping("/notices")
   public String createNotice(@ModelAttribute NoticeDto notice, HttpSession session) {
       try {
           UserAccountDto user = (UserAccountDto) session.getAttribute("loggedInUser");
           String userRole = (String) session.getAttribute("userRole");
           
           if (user == null || !"ADMIN".equals(userRole)) {
               return "redirect:/notices";
           }
           
           notice.setCreatedDate(LocalDateTime.now());
           notice.setAuthorId(user.getEmployeeId());
           noticeService.createNotice(notice);
           
           log.info("Notice created by user: {}", user.getEmployeeId());
           return "redirect:/notices";
       } catch (Exception e) {
           log.error("Error in createNotice: ", e);
           return "error";
       }
   }
   
   @GetMapping("/notices/{id}/edit")
   public String editNoticeForm(@PathVariable("id") int noticeId, Model model, HttpSession session) {
       try {
           UserAccountDto user = (UserAccountDto) session.getAttribute("loggedInUser");
           String userRole = (String) session.getAttribute("userRole");
           
           if (user == null || !"ADMIN".equals(userRole)) {
               return "redirect:/notices";
           }
           
           model.addAttribute("notice", noticeService.getNoticeById(noticeId));
           return "notice/form";
       } catch (Exception e) {
           log.error("Error in editNoticeForm: ", e);
           return "error";
       }
   }
   
   @PostMapping("/notices/{id}")
   public String updateNotice(@PathVariable("id") int noticeId, 
                            @ModelAttribute NoticeDto notice,
                            HttpSession session) {
       try {
           UserAccountDto user = (UserAccountDto) session.getAttribute("loggedInUser");
           String userRole = (String) session.getAttribute("userRole");
           
           if (user == null || !"ADMIN".equals(userRole)) {
               return "redirect:/notices";
           }
           
           notice.setNoticeId(noticeId);
           noticeService.updateNotice(notice);
           
           log.info("Notice {} updated by user: {}", noticeId, user.getEmployeeId());
           return "redirect:/notices";
       } catch (Exception e) {
           log.error("Error in updateNotice: ", e);
           return "error";
       }
   }
   
   @PostMapping("/notices/{id}/delete")
   public String deleteNotice(@PathVariable("id") int noticeId, HttpSession session) {
       try {
           UserAccountDto user = (UserAccountDto) session.getAttribute("loggedInUser");
           String userRole = (String) session.getAttribute("userRole");
           
           if (user == null || !"ADMIN".equals(userRole)) {
               return "redirect:/notices";
           }
           
           noticeService.deleteNotice(noticeId);
           
           log.info("Notice {} deleted by user: {}", noticeId, user.getEmployeeId());
           return "redirect:/notices";
       } catch (Exception e) {
           log.error("Error in deleteNotice: ", e);
           return "error";
       }
   }
   
   @GetMapping("/notices/search")
   public String searchNotices(
           @RequestParam(value = "searchType", required = false) String searchType,
           @RequestParam(value = "keyword", required = false) String keyword, 
           Model model,
           HttpSession session) {
       try {
           UserAccountDto user = (UserAccountDto) session.getAttribute("loggedInUser");
           String userRole = (String) session.getAttribute("userRole");
           
           if (user == null) {
               return "redirect:/login";
           }

           List<NoticeDto> notices;
           if (keyword != null && !keyword.trim().isEmpty()) {
               notices = noticeService.searchNotices(searchType, keyword);
           } else {
               notices = noticeService.getAllNotices();
           }
           
           model.addAttribute("notices", notices);
           model.addAttribute("userRole", userRole);
           
           return "notice/list";
       } catch (Exception e) {
           log.error("Error in searchNotices: ", e);
           return "error";
       }
   }
}