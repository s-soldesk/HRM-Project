package com.hrm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.hrm.dto.EmployeeDto;
import com.hrm.dto.SalaryDto;
import com.hrm.security.CustomUserDetails;
import com.hrm.service.SalaryService;

@Controller
@RequestMapping("/salary")
public class SalaryController {
	
	@Autowired
	SalaryService salaryService; 
	
	// 인사 전용 - 모든 사원 급여 조회
	@GetMapping("/manage")
	public String manageSalaries(Model m) {
		List<SalaryDto> salaries = salaryService.getAllSalaries();
		m.addAttribute("salaries", salaries);
		return "salary/manage";
	}
	
	// 사원 전용 - 개인 사원 급여 조회
	@GetMapping("/view/{employeeId}")
	public String viewSalary(@PathVariable("employeeId") Integer employeeId,
			Model m) {
		SalaryDto salary = salaryService.getSalaryByEmployeeId(employeeId);
		m.addAttribute("salary", salary);
		return "salary/view";
	}
	
	// 인사 전용 - 급여 수정 페이지
	@GetMapping("/edit/{employeeId}")
	public String editSalary(@PathVariable("employeeId") Integer employeeId, Model m) {
	    SalaryDto salary = salaryService.getSalaryByEmployeeId(employeeId);
	    m.addAttribute("salary", salary);
	    return "salary/edit";
	}
	
	// 인사 전용 - 급여 수정 처리
	@PostMapping("/edit")
	public String updateSalary(@ModelAttribute SalaryDto salaryDto) {
		salaryService.updateSalary(salaryDto);
		return "redirect:/salary/manage";
	}
	
	// 인사 전용 - 새로운 급여 추가 페이지
	@GetMapping("/add")
	public String addSalaryForm(Model m) {
		m.addAttribute("salary", new SalaryDto());
		return "salary/add";
	}
	
	// 인사 전용 - 새로운 급여 추가 처리
	@PostMapping("/add")
	public String addSalary(@ModelAttribute SalaryDto salaryDto) {
		salaryService.addSalary(salaryDto);
		return "redirect:/salary/manage";
	}
	
	// 인사 전용 - 급여 관련 삭제 처리
	@GetMapping("/delete/{employeeId}")
	public String deleteSalary(@PathVariable("employeeId") Integer employeeId) {
	    salaryService.deleteSalary(employeeId);
	    return "redirect:/salary/manage";
	}
	
	@GetMapping("/list")
    public String getMonthlySalaryList(@RequestParam(required = false) Integer employeeId, 
                                     Model m) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isHR = auth.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_HR"));
        
        if (employeeId == null && !isHR) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            employeeId = ((CustomUserDetails) userDetails).getEmployeeId();
        }
        
        List<SalaryDto> salaries = salaryService.getSalariesByEmployeeId(employeeId);
        m.addAttribute("salariesByMonth", salaryService.getSalariesByEmployeeId(employeeId));
        return "salary/monthlySalaryList";
    }
    
    @GetMapping("/detail/{salaryId}")
    public String getSalaryDetail(@PathVariable Integer salaryId, Model model) {
        SalaryDto salary = salaryService.getSalaryById(salaryId);
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isHR = auth.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_HR"));
        
        if (!isHR) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            Integer currentEmployeeId = ((CustomUserDetails) userDetails).getEmployeeId();
            
            if (!salary.getEmployeeId().equals(currentEmployeeId)) {
                throw new AccessDeniedException("접근 권한이 없습니다.");
            }
        }
        
        model.addAttribute("salary", salary);
        return "salary/salaryDetail";
    }
}
