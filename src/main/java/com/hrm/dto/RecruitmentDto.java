package com.hrm.dto;

import java.time.LocalDate;

import com.hrm.enums.PostStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RecruitmentDto {
	private Integer id;
	private String title;
	private String content;
	private PostStatus status;
	private LocalDate endDate;
	private String createdBy;
	private LocalDate createdAt;
}
