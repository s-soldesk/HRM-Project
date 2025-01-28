package com.hrm.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hrm.dto.EmployeeDto;
import com.hrm.dto.MessageDto;
import com.hrm.mapper.MessageMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageMapper messageMapper;
    
    public List<EmployeeDto> getAllEmployeesWithLastMessage(Integer currentUserId) {
        try {
            List<EmployeeDto> employees = messageMapper.getAllEmployees();
            for (EmployeeDto emp : employees) {
                MessageDto lastMessage = messageMapper.getLastMessageBetweenUsers(currentUserId, emp.getEmployeeId());
                emp.setLastMessage(lastMessage != null ? lastMessage.getContent() : null);
            }
            return employees;
        } catch (Exception e) {
            log.error("Error getting employees with last messages: ", e);
            throw new RuntimeException("Failed to get employees with last messages", e);
        }
    }
    
    @Transactional
    public void sendMessage(MessageDto message) {
        try {
            log.info("Sending message: {}", message);
            
            if (message.getSenderId() == null || message.getReceiverId() == null) {
                throw new IllegalArgumentException("Sender or Receiver ID cannot be null");
            }
            
            if (message.getContent() == null || message.getContent().trim().isEmpty()) {
                throw new IllegalArgumentException("Message content cannot be empty");
            }
            
            message.setSentTime(LocalDateTime.now());
            message.setIsRead(false);
            
            messageMapper.sendMessage(message);
            log.info("Message sent successfully");
            
        } catch (Exception e) {
            log.error("Error sending message: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send message", e);
        }
    }
    
    public List<EmployeeDto> getAllEmployees() {
        try {
            log.info("Getting all employees");
            List<EmployeeDto> employees = messageMapper.getAllEmployees();
            log.info("Found {} employees", employees.size());
            return employees;
        } catch (Exception e) {
            log.error("Error getting all employees: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get employees", e);
        }
    }
    
    public List<MessageDto> getReceivedMessages(Integer receiverId) {
        try {
            log.info("Getting received messages for user: {}", receiverId);
            return messageMapper.getReceivedMessages(receiverId);
        } catch (Exception e) {
            log.error("Error getting received messages: ", e);
            throw new RuntimeException("Failed to get received messages", e);
        }
    }
    
    public List<MessageDto> getSentMessages(Integer senderId) {
        try {
            log.info("Getting sent messages for user: {}", senderId);
            return messageMapper.getSentMessages(senderId);
        } catch (Exception e) {
            log.error("Error getting sent messages: ", e);
            throw new RuntimeException("Failed to get sent messages", e);
        }
    }
    
    public MessageDto getMessage(Integer messageId) {
        try {
            log.info("Getting message: {}", messageId);
            return messageMapper.getMessage(messageId);
        } catch (Exception e) {
            log.error("Error getting message: ", e);
            throw new RuntimeException("Failed to get message", e);
        }
    }
    
    @Transactional
    public void markAsRead(Integer messageId) {
        try {
            log.info("Marking message as read: {}", messageId);
            messageMapper.markAsRead(messageId);
        } catch (Exception e) {
            log.error("Error marking message as read: ", e);
            throw new RuntimeException("Failed to mark message as read", e);
        }
    }
    
    public List<MessageDto> getChatMessages(Integer senderId, Integer receiverId) {
        try {
            log.info("Getting chat messages between {} and {}", senderId, receiverId);
            return messageMapper.getChatMessages(senderId, receiverId);
        } catch (Exception e) {
            log.error("Error getting chat messages: ", e);
            throw new RuntimeException("Failed to get chat messages", e);
        }
    }
}