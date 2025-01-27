package com.hrm.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hrm.dto.EmployeeDto;
import com.hrm.dto.MessageDto;
import com.hrm.mapper.MessageMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageMapper messageMapper;
    
    public void sendMessage(MessageDto message) {
        message.setSentTime(LocalDateTime.now());
        message.setIsRead(false);
        messageMapper.sendMessage(message);
    }
    
    public List<MessageDto> getReceivedMessages(Integer receiverId) {
        return messageMapper.getReceivedMessages(receiverId);
    }
    
    public List<MessageDto> getSentMessages(Integer senderId) {
        return messageMapper.getSentMessages(senderId);
    }
    
    public MessageDto getMessage(Integer messageId) {
        return messageMapper.getMessage(messageId);
    }
    
    public void markAsRead(Integer messageId) {
        messageMapper.markAsRead(messageId);
    }
    
    public List<EmployeeDto> getAllEmployees() {
        return messageMapper.getAllEmployees();
    }
}