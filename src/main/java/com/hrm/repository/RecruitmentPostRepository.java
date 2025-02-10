package com.hrm.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hrm.entity.RecruitmentPostEntity;
import com.hrm.enums.PostStatus;

@Repository
public interface RecruitmentPostRepository extends JpaRepository<RecruitmentPostEntity, Integer> {

	// status 로 모든 채용공고를 찾는 메소드
	List<RecruitmentPostEntity> findByStatus(PostStatus status);

	// 기간이 지났을 때 상태를 변경하기 위한 메소드
	List<RecruitmentPostEntity> findByEndDate(LocalDate date);
}
