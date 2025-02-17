package com.hrm.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.hrm.dto.EmployeeDto;
import com.hrm.dto.MessageDto;

@Mapper
public interface MessageDao {
    List<MessageDto> getReceivedMessages(Integer receiverId);
    List<MessageDto> getSentMessages(Integer senderId);
    void sendMessage(MessageDto message);
    MessageDto getMessage(Integer messageId);
    void markAsRead(Integer messageId);
    List<EmployeeDto> getAllEmployees();
    List<MessageDto> getChatMessages(@Param("senderId") Integer senderId, @Param("receiverId") Integer receiverId);
    MessageDto getLastMessageBetweenUsers(@Param("user1Id") Integer user1Id, @Param("user2Id") Integer user2Id);
    }