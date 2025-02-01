package com.hrm.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private int commentId;
    private int inquiryId;
    private String content;
    private int authorId;
    private LocalDateTime createdDate;
    private int readcount;
    private String authorName;
}
