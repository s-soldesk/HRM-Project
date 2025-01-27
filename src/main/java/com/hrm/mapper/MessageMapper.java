package com.hrm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.hrm.dto.EmployeeDto;
import com.hrm.dto.MessageDto;

@Mapper
public interface MessageMapper {
    List<MessageDto> getReceivedMessages(Integer receiverId);
    List<MessageDto> getSentMessages(Integer senderId);
    void sendMessage(MessageDto message);
    MessageDto getMessage(Integer messageId);  // @Select 어노테이션 제거
    void markAsRead(Integer messageId);
    List<EmployeeDto> getAllEmployees();
}