package com.hrm.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hrm.dto.CommentDto;
import com.hrm.dto.HrInquiryDto;
import com.hrm.dto.NoticeDto;
import com.hrm.mapper.HrInquiryMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HrInquiryService {
	   private final HrInquiryMapper hrInquiryMapper;
	   
	   public List<HrInquiryDto> getAllInquiries() {
	       return hrInquiryMapper.getAllInquiries();
	   }
	   
	   public HrInquiryDto getInquiryById(int inquiryId) {
	       return hrInquiryMapper.getInquiryById(inquiryId);
	   }
	   
	   public void createInquiry(HrInquiryDto inquiry) {
	       hrInquiryMapper.insertInquiry(inquiry);
	   }
	   
	   public void updateInquiry(HrInquiryDto inquiry) {
	       hrInquiryMapper.updateInquiry(inquiry);
	   }

	   public void deleteInquiry(int inquiryId) {
	       hrInquiryMapper.deleteInquiry(inquiryId);
	   }
	   
	   public List<CommentDto> getCommentsByInquiryId(int inquiryId) {
	       return hrInquiryMapper.getCommentsByInquiryId(inquiryId);
	   }
	   
	   public void addComment(CommentDto comment) {
	       hrInquiryMapper.insertComment(comment);
	   }
	   
	    public List<HrInquiryDto> searchInquiries(String searchType, String keyword) {
	        return hrInquiryMapper.searchInquiries(searchType, keyword);
	    }
	}