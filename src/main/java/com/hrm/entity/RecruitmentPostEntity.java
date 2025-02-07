package com.hrm.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;

import com.hrm.enums.PostStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity // 이 클래스는 JPA 엔터티. 데이터베이스 테이블과 매핑
@Getter
@Setter
@Table(name = "RecruitmentPost")
public class RecruitmentPostEntity {
	@Id // 기본 키
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 기본키 생성 전략. IDENTITY는 자동 증가
	private Long id;

	private String title;

	@Column(columnDefinition = "TEXT") // 텍스트타입
	private String content;

	@Enumerated(EnumType.STRING) // enum 값을 문자열로 저장
	private PostStatus status;

	private LocalDate startDate;
	private LocalDate endDate;

	private String createdBy;

	@CreatedDate // 엔터티가 생성될 때 자동으로 현재시간 저장
	private LocalDateTime createdAt;

}
