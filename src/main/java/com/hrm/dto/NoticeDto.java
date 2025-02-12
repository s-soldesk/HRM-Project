package com.hrm.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoticeDto {
    private int noticeId;
    private String title;
    private String content;
    private int authorId;
    private LocalDateTime createdDate;
    private int readCount;
    private String authorName;
}
