package com.hrm.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hrm.entity.RecruitmentPostEntity;
import com.hrm.enums.PostStatus;
import com.hrm.repository.RecruitmentPostRepository;

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
		post.setStartDate(updatedPost.getStartDate());
		post.setEndDate(updatedPost.getEndDate());
		return post;
	}

	@Transactional
	public void deletePost(Integer id) {
		recruitmentPostRepository.deleteById(id);
	}

}
