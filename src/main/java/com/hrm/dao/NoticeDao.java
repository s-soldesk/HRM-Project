package com.hrm.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.hrm.dto.NoticeDto;

@Mapper
public interface NoticeDao {
    List<NoticeDto> getAllNotices();
    NoticeDto getNoticeById(int noticeId);
    void insertNotice(NoticeDto notice);
    void updateNotice(NoticeDto notice);
    void deleteNotice(int noticeId);
    void increaseReadCount(int noticeId);
    
    List<NoticeDto> searchNotices(@Param("searchType") String searchType, 
            @Param("keyword") String keyword);
}
