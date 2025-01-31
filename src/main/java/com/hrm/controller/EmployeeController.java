package com.hrm.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hrm.dto.EmployeeDto;
import com.hrm.service.EmployeeService;
import com.hrm.service.UserAccountService;

import jakarta.servlet.http.HttpSession;

import org.springframework.ui.Model;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class EmployeeController {
    
    private final EmployeeService employeeService;
    private final UserAccountService userAccountService;
    
    @Value("${file.upload.dir:uploads/profiles}")
    private String uploadDir;

    @GetMapping("/profile")
    public String getProfile(HttpSession session, Model model) {
        Integer employeeId = (Integer) session.getAttribute("loggedInEmail");
        if (employeeId != null) {
            try {
                EmployeeDto employee = employeeService.getEmployeeById(employeeId);
                model.addAttribute("employee", employee);
                return "employee/profile";
            } catch (Exception e) {
                log.error("Error fetching profile: ", e);
                return "redirect:/login";
            }
        }
        return "redirect:/login";
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
            @RequestParam(value = "profileImage", required = false) MultipartFile file,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Integer employeeId = (Integer) session.getAttribute("loggedInEmail");
        try {
            if (employeeId == null) {
                return "redirect:/login";
            }

            EmployeeDto employee = employeeService.getEmployeeById(employeeId);
            if (employee == null) {
                redirectAttributes.addFlashAttribute("error", "프로필을 찾을 수 없습니다.");
                return "redirect:/profile?error";
            }

            // 로그 추가
            log.info("프로필 업데이트 시작 - employeeId: {}", employeeId);
            
            if (email != null && !email.trim().isEmpty()) {
                employee.setEmail(email.trim());
                log.info("이메일 업데이트: {}", email.trim());
            }
            if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
                employee.setPhoneNumber(phoneNumber.trim());
                log.info("전화번호 업데이트: {}", phoneNumber.trim());
            }
            
            if (file != null && !file.isEmpty()) {
                String imagePath = saveProfileImage(file, String.valueOf(employeeId));
                employee.setProfileImage(imagePath);
                log.info("프로필 이미지 업데이트: {}", imagePath);
            }

            // 업데이트 전 상태 로그
            log.info("업데이트 전 employee 상태: {}", employee);
            
            employeeService.updateProfile(employee);
            
            // 업데이트 후 검증
            EmployeeDto updatedEmployee = employeeService.getEmployeeById(employeeId);
            log.info("업데이트 후 employee 상태: {}", updatedEmployee);

            redirectAttributes.addFlashAttribute("success", "프로필이 성공적으로 업데이트되었습니다.");
            return "redirect:/profile?success";
        } catch (Exception e) {
            log.error("Error updating profile for employee {}: ", employeeId, e);
            redirectAttributes.addFlashAttribute("error", "프로필 업데이트 중 오류가 발생했습니다.");
            return "redirect:/profile?error";
        }
    }

    private String saveProfileImage(MultipartFile file, String employeeId) throws IOException {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            log.info("Upload directory created: {}", uploadPath);
        }
        
        String fileName = employeeId + "_" + System.currentTimeMillis() + getFileExtension(file.getOriginalFilename());
        Path filePath = uploadPath.resolve(fileName);
        
        log.info("Saving file to: {}", filePath);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        return fileName;
    }

    @PostMapping("/profile/password")
    public String changePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            Integer employeeId = (Integer) session.getAttribute("loggedInEmail");
            if (employeeId == null) {
                return "redirect:/login";
            }

            boolean success = userAccountService.changePassword(employeeId, currentPassword, newPassword);
            if (success) {
                session.invalidate();
                return "redirect:/login?passwordChanged";
            } else {
                redirectAttributes.addFlashAttribute("error", "현재 비밀번호가 일치하지 않습니다.");
                return "redirect:/profile?pwError";
            }
        } catch (Exception e) {
            log.error("Error changing password: ", e);
            redirectAttributes.addFlashAttribute("error", "비밀번호 변경 중 오류가 발생했습니다.");
            return "redirect:/profile?pwError";
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return ".jpg";
        }
        return fileName.substring(fileName.lastIndexOf('.'));
    }
}