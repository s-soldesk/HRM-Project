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

import com.hrm.entity.RecruitmentPostEntity;
import com.hrm.service.RecruitmentPostService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/recruitments")
@RequiredArgsConstructor
public class RecruitmentPostController {

	private final RecruitmentPostService recruitmentPostService;

	@GetMapping("")
	public String getList(Model m) {
		List<RecruitmentPostEntity> posts = recruitmentPostService.getAllPosts();
		m.addAttribute("posts", posts);
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
		RecruitmentPostEntity savePost = recruitmentPostService.createPost(post, user.getName());
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
