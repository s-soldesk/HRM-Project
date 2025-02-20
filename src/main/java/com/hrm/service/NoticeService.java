package com.hrm.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hrm.dto.NoticeDto;
import com.hrm.dao.NoticeDao;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeDao noticeDao;
    
    public List<NoticeDto> getAllNotices() {
        return noticeDao.getAllNotices();
    }
    
    public NoticeDto getNoticeById(int noticeId) {
        return noticeDao.getNoticeById(noticeId);
    }
    
    public void createNotice(NoticeDto notice) {
        noticeDao.insertNotice(notice);
    }
    
    public void updateNotice(NoticeDto notice) {
        noticeDao.updateNotice(notice);
    }
    
    public void deleteNotice(int noticeId) {
        noticeDao.deleteNotice(noticeId);
    }
    
    public List<NoticeDto> searchNotices(String searchType, String keyword) {
        return noticeDao.searchNotices(searchType, keyword);
       
    }
    
    @Transactional
    public NoticeDto getNoticeWithIncreasedReadCount(int noticeId) {
    	noticeDao.increaseReadCount(noticeId);
        return noticeDao.getNoticeById(noticeId);
    }
}