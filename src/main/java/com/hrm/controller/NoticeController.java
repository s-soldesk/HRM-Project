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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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

   // 현재 로그인한 사용자의 ID를 가져오는 메소드
   private String getCurrentUserId() {
       Authentication auth = SecurityContextHolder.getContext().getAuthentication();
       if (auth != null && auth.isAuthenticated()) {
           log.info("Current user: {}", auth.getName());
           return auth.getName();
       }
       throw new RuntimeException("No authenticated user found");
   }

   // 현재 사용자가 관리자인지 확인하는 메소드
   private boolean isAdmin() {
       Authentication auth = SecurityContextHolder.getContext().getAuthentication();
       boolean isAdmin = auth != null && auth.getAuthorities().stream()
               .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || 
                         a.getAuthority().equals("ROLE_Admin") ||
                         a.getAuthority().equals("ROLE_admin"));
       log.info("Checking admin authority. User: {}, Authorities: {}, IsAdmin: {}", 
               auth.getName(), auth.getAuthorities(), isAdmin);
       return isAdmin;
   }

   @GetMapping("/notices")
   public String listNotices(Model model) {
       try {
           String currentUserId = getCurrentUserId();
           List<NoticeDto> notices = noticeService.getAllNotices();
           model.addAttribute("notices", notices);
           model.addAttribute("isAdmin", isAdmin());
           
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
               model.addAttribute("isAdmin", isAdmin());
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
           if (!isAdmin()) {
               return "redirect:/notices";
           }
           return "notice/form";
       } catch (Exception e) {
           log.error("Error in newNoticeForm: ", e);
           return "error";
       }
   }

   @PostMapping("/notices")
   public String createNotice(@ModelAttribute NoticeDto notice) {
       try {
           if (!isAdmin()) {
               return "redirect:/notices";
           }
           notice.setCreatedDate(LocalDateTime.now());
           notice.setAuthorId(1); // admin 사용자의 ID를 1로 고정
           noticeService.createNotice(notice);
           log.info("Notice created successfully by {}", getCurrentUserId());
           return "redirect:/notices";
       } catch (Exception e) {
           log.error("Error in createNotice: ", e);
           return "error";
       }
   }

   @GetMapping("/notices/{id}/edit")
   public String editNoticeForm(@PathVariable("id") int noticeId, Model model) {
       try {
           if (!isAdmin()) {
               return "redirect:/notices";
           }
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
   public String updateNotice(@PathVariable("id") int noticeId, @ModelAttribute NoticeDto notice) {
       try {
           if (!isAdmin()) {
               return "redirect:/notices";
           }
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
           if (!isAdmin()) {
               return "redirect:/notices";
           }
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
           model.addAttribute("isAdmin", isAdmin());
           
           return "notice/list";
       } catch (Exception e) {
           log.error("Error in searchNotices: ", e);
           return "error";
       }
   }
}