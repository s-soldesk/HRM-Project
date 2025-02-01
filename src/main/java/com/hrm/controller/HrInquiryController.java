package com.hrm.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hrm.dto.CommentDto;
import com.hrm.dto.HrInquiryDto;
import com.hrm.dto.NoticeDto;
import com.hrm.service.HrInquiryService;
import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HrInquiryController {
	   private final HrInquiryService hrInquiryService;
	   
	   @GetMapping("/hrInquiry")
	   public String listInquiries(Model model) {
	       List<HrInquiryDto> inquiries = hrInquiryService.getAllInquiries();
	       model.addAttribute("inquiries", inquiries);
	       return "hrInquiry/list";
	   }
	   
	   @GetMapping("/hrInquiry/{id}")
	   public String viewInquiry(@PathVariable("id") int inquiryId, Model model) {
	       model.addAttribute("inquiry", hrInquiryService.getInquiryById(inquiryId));
	       model.addAttribute("comments", hrInquiryService.getCommentsByInquiryId(inquiryId));
	       return "hrInquiry/view";
	   }
	   
	   @GetMapping("/hrInquiry/new")
	   public String newInquiryForm() {
	       return "hrInquiry/form";
	   }
	   
	   @PostMapping("/hrInquiry")
	   public String createInquiry(@ModelAttribute HrInquiryDto inquiry) {
	       inquiry.setCreatedDate(LocalDateTime.now());
	       hrInquiryService.createInquiry(inquiry);
	       return "redirect:/hrInquiry";
	   }
	   
	   @PostMapping("/hrInquiry/{inquiryId}/comments")
	   public String addComment(@PathVariable("inquiryId") int inquiryId, 
	                          @ModelAttribute CommentDto comment) {
	       comment.setInquiryId(inquiryId);
	       comment.setCreatedDate(LocalDateTime.now());
	       hrInquiryService.addComment(comment);
	       return "redirect:/hrInquiry/" + inquiryId;
	   }
	   @GetMapping("/hrInquiry/search") 
	   public String searchInquiries(
	       @RequestParam("searchType") String searchType, 
	       @RequestParam("keyword") String keyword,
	       Model model) {
	       List<HrInquiryDto> inquiries = hrInquiryService.searchInquiries(searchType, keyword);
	       model.addAttribute("inquiries", inquiries);
	       return "hrInquiry/list";
	   }
	   
	   @GetMapping("/hrInquiry/edit/{id}")
	   public String editForm(@PathVariable("id") int inquiryId, Model model) {
	       model.addAttribute("inquiry", hrInquiryService.getInquiryById(inquiryId));
	       return "hrInquiry/edit";
	   }

	   @PostMapping("/hrInquiry/update/{id}")
	   public String updateInquiry(@PathVariable("id") int inquiryId, @ModelAttribute HrInquiryDto inquiry) {
	       inquiry.setInquiryId(inquiryId);
	       hrInquiryService.updateInquiry(inquiry);
	       return "redirect:/hrInquiry";
	   }

	   @GetMapping("/hrInquiry/delete/{id}")
	   public String deleteInquiry(@PathVariable("id") int inquiryId) {
	       hrInquiryService.deleteInquiry(inquiryId);
	       return "redirect:/hrInquiry";
	   }
	   
	}