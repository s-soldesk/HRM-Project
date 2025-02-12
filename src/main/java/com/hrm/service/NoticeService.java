package com.hrm.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hrm.dto.NoticeDto;
import com.hrm.mapper.NoticeMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeMapper noticeMapper;
    
    public List<NoticeDto> getAllNotices() {
        return noticeMapper.getAllNotices();
    }
    
    public NoticeDto getNoticeById(int noticeId) {
        return noticeMapper.getNoticeById(noticeId);
    }
    
    public void createNotice(NoticeDto notice) {
        noticeMapper.insertNotice(notice);
    }
    
    public void updateNotice(NoticeDto notice) {
        noticeMapper.updateNotice(notice);
    }
    
    public void deleteNotice(int noticeId) {
        noticeMapper.deleteNotice(noticeId);
    }
    
    public List<NoticeDto> searchNotices(String searchType, String keyword) {
        return noticeMapper.searchNotices(searchType, keyword);
       
    }
    
    @Transactional
    public NoticeDto getNoticeWithIncreasedReadCount(int noticeId) {
    	noticeMapper.increaseReadCount(noticeId);
        return noticeMapper.getNoticeById(noticeId);
    }
}