package com.hrm.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.hrm.dto.EmployeeDto;
import com.hrm.service.ProfileService;
import com.hrm.service.UserAccountService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ProfileController {
    
    private final ProfileService profileService;
    private final UserAccountService userAccountService;
    
    @Value("${file.upload.dir:uploads/profiles}")
    private String uploadDir;

    // 현재 로그인한 사용자의 ID를 가져오는 메소드
    private Integer getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            log.info("Current user: {}", auth.getName());
            return Integer.valueOf(auth.getName());
        }
        throw new RuntimeException("No authenticated user found");
    }

    @GetMapping("/profile")
    public String getProfile(Model model) {
        try {
            Integer currentUserId = getCurrentUserId();
            EmployeeDto employee = profileService.getEmployeeById(currentUserId);
            model.addAttribute("employee", employee);
            return "profile/profile";
        } catch (Exception e) {
            log.error("Error fetching profile: ", e);
            return "error";
        }
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            @RequestParam(value = "profileImage", required = false) MultipartFile file,
            RedirectAttributes redirectAttributes) {
        try {
            Integer currentUserId = getCurrentUserId();
            EmployeeDto employee = profileService.getEmployeeById(currentUserId);
            
            if (employee == null) {
                redirectAttributes.addFlashAttribute("error", "프로필을 찾을 수 없습니다.");
                return "redirect:/profile?error";
            }

            if (file != null && !file.isEmpty()) {
                String imagePath = saveProfileImage(file, String.valueOf(currentUserId));
                employee.setProfileImage(imagePath);
                profileService.updateProfile(employee);
            }

            redirectAttributes.addFlashAttribute("success", "프로필이 성공적으로 업데이트되었습니다.");
            return "redirect:/profile?success";
        } catch (Exception e) {
            log.error("Error updating profile: ", e);
            redirectAttributes.addFlashAttribute("error", "프로필 업데이트 중 오류가 발생했습니다.");
            return "redirect:/profile?error";
        }
    }

    @PostMapping("/profile/password")
    public String changePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            RedirectAttributes redirectAttributes) {
        try {
            Integer currentUserId = getCurrentUserId();
            boolean success = userAccountService.changePassword(currentUserId, currentPassword, newPassword);
            
            if (success) {
                redirectAttributes.addFlashAttribute("success", "비밀번호가 성공적으로 변경되었습니다.");
                return "redirect:/profile?pwSuccess";
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
    
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return ".jpg";
        }
        return fileName.substring(fileName.lastIndexOf('.'));
    }
}