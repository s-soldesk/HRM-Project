package com.hrm.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hrm.entity.RecruitmentPostEntity;
import com.hrm.enums.PostStatus;
import com.hrm.service.EmployeeService;
import com.hrm.service.RecruitmentPostService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/recruitments")
@RequiredArgsConstructor
public class RecruitmentPostController {

	private final RecruitmentPostService recruitmentPostService;
	private final EmployeeService employeeService;

	@GetMapping("")
	public String getList(@RequestParam(name = "status", required = false) PostStatus status, Model m) {
		List<RecruitmentPostEntity> posts;

		if (status != null) { // 상태를 선택했으면 해당 상태로 필터링
			posts = recruitmentPostService.getPostsByStatus(status);
		} else { // 기본값은 전체 게시물
			posts = recruitmentPostService.getAllPosts();
		}

		m.addAttribute("posts", posts);
		m.addAttribute("selectedStatus", status);
		return "recruitments/list";
	}

	@GetMapping("/{id}")
	public String getDetail(@PathVariable("id") Integer id, Model m) {
		RecruitmentPostEntity post = recruitmentPostService.getPost(id);
		m.addAttribute("post", post);
		return "recruitments/detail";
	}

	@GetMapping("/add")
	public String createForm(Model m) {
		RecruitmentPostEntity post = new RecruitmentPostEntity(); // 빈 객체 전달
		m.addAttribute("post", post);
		return "recruitments/form";
	}

	@PostMapping("/add")
	public String createPost(@ModelAttribute RecruitmentPostEntity post, Principal user) { // Principal 로 사용자 식별 정보 가져오기
		String employeeName = employeeService.getEmployeeName(user.getName());
		RecruitmentPostEntity savePost = recruitmentPostService.createPost(post, employeeName);
		return "redirect:/recruitments/" + savePost.getId();
	}

	@GetMapping("/edit/{id}")
	public String updateForm(@PathVariable("id") int id, Model m) {
		RecruitmentPostEntity post = recruitmentPostService.getPost(id);
		m.addAttribute("post", post);
		return "recruitments/form";
	}

	@PutMapping("/edit/{id}")
	public String updatePost(@PathVariable("id") int id, @ModelAttribute RecruitmentPostEntity post) {
		RecruitmentPostEntity savePost = recruitmentPostService.updatePost(id, post);
		return "redirect:/recruitments/" + savePost.getId();
	}

	@DeleteMapping("/{id}")
	public String deletePost(@PathVariable("id") Integer id) {
		recruitmentPostService.deletePost(id);
		return "redirect:/recruitments";
	}

}
