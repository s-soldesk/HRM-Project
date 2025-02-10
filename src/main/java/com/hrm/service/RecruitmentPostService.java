package com.hrm.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hrm.entity.RecruitmentPostEntity;
import com.hrm.enums.PostStatus;
import com.hrm.repository.RecruitmentPostRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
/*
 * 클래스 레벨의 readOnly=true 는 기본적으로 모든 메소드가 읽기 전용임을 설정, 쓰기가 필요한 특정 메소드에서만
 * 별도로 @Transactional을 설정하여 오버라이드하는 패턴.(DB가 리소스를 더 효율적으로 사용)
 */
public class RecruitmentPostService {

	private final RecruitmentPostRepository recruitmentPostRepository;

	public List<RecruitmentPostEntity> getAllPosts() {
		return recruitmentPostRepository.findAll();
	}

	public List<RecruitmentPostEntity> getPostsByStatus(PostStatus status) { // 상태로 게시글 찾기
		return recruitmentPostRepository.findByStatus(status);
	}

	public RecruitmentPostEntity getPost(Integer id) {
		return recruitmentPostRepository.findById(id).orElseThrow(() -> new IllegalArgumentException());
	}

	/*
	 * 서버 시작하면 게시글 상태 함수 초기화 (서버를 항상 켜놓지 않는 개발 시에만)
	 */
	@PostConstruct
	public void initializeStatus() {
		updateStatus();
	}

	/*
	 * 마감 날짜가 지나면 자동으로 상태를 변경하기 위한 스케줄러 설정
	 */
	@Scheduled(cron = "0 0 0 * * *") // 매일 자정에 실행
	public void updateStatus() {
		LocalDate today = LocalDate.now();

		List<RecruitmentPostEntity> expired = recruitmentPostRepository.findByEndDate(today);

		expired.forEach(post -> {
			post.setStatus(PostStatus.CLOSED);
			recruitmentPostRepository.save(post);
		});
	}

	@Transactional // 데이터를 변경하는 작업. (삽입)
	public RecruitmentPostEntity createPost(RecruitmentPostEntity post) {
		return recruitmentPostRepository.save(post);
	}

	@Transactional
	public RecruitmentPostEntity updatePost(Integer id, RecruitmentPostEntity updatedPost) {
		RecruitmentPostEntity post = getPost(id);
		post.setTitle(updatedPost.getTitle());
		post.setContent(updatedPost.getContent());
		post.setStatus(updatedPost.getStatus());
		post.setEndDate(updatedPost.getEndDate());
		return post;
	}

	@Transactional
	public void deletePost(Integer id) {
		recruitmentPostRepository.deleteById(id);
	}

}
