package com.hrm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hrm.entity.RecruitmentPostEntity;
import com.hrm.enums.PostStatus;

@Repository
public interface RecruitmentPostRepository extends JpaRepository<RecruitmentPostEntity, Integer> {
	// status 로 모든 채용공고를 찾는 메소드
	List<RecruitmentPostEntity> findByStatus(PostStatus status);
}
