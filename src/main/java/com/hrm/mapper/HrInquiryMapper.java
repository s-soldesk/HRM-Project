package com.hrm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.hrm.dto.CommentDto;
import com.hrm.dto.HrInquiryDto;

@Mapper
public interface HrInquiryMapper {
    List<HrInquiryDto> getAllInquiries();
    HrInquiryDto getInquiryById(int inquiryId);
    void insertInquiry(HrInquiryDto inquiry);
    void updateInquiry(HrInquiryDto inquiry);
    void deleteInquiry(int inquiryId);
    List<CommentDto> getCommentsByInquiryId(int inquiryId);
    void insertComment(CommentDto comment);
    void deleteComment(int commentId);
    
    List<HrInquiryDto> searchInquiries(@Param("searchType") String searchType, 
            @Param("keyword") String keyword);
}
