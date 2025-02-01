package com.hrm.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HrInquiryDto {
    private int inquiryId;
    private String title;
    private String content;
    private int authorId;
    private LocalDateTime createdDate;
    private int readcount;
    private String authorName;
}
