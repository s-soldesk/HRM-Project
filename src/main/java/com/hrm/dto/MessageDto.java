package com.hrm.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private Integer messageId;
    private Integer senderId;
    private Integer receiverId;
    private String content;
    private LocalDateTime sentTime;
    private Boolean isRead;
    private String senderName;    // 보낸 사람 이름
    private String receiverName;  // 받는 사람 이름
}