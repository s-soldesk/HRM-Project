package com.hrm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hrm.dto.UserAccountDto;
import com.hrm.service.UserAccountService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class LoginController {
   
    @Autowired
    private UserAccountService userAccountService;
    
    @GetMapping("/login")
    public String loginPage() {
        return "login/login";
    }
    
    @PostMapping("/login")
    public String login(@RequestParam("employeeId") String employeeId, 
                       @RequestParam("password") String password, 
                       HttpServletRequest request,
                       HttpServletResponse response) {
        
        try {
            UserAccountDto user = userAccountService.login(employeeId, password);
            
            if (user != null) {
                // 기존 세션 제거
                HttpSession oldSession = request.getSession(false);
                if (oldSession != null) {
                    oldSession.invalidate();
                }
                
                // 새로운 세션 생성
                HttpSession newSession = request.getSession(true);
                newSession.setMaxInactiveInterval(30 * 60); // 30분
                
                // 세션에 사용자 정보 저장
                newSession.setAttribute("loggedInUser", user);
                newSession.setAttribute("loggedInEmail", employeeId);
                newSession.setAttribute("employeeId", Integer.parseInt(employeeId));
                
                // 사용자 역할 정보 저장
                newSession.setAttribute("userRole", user.getRole().toUpperCase());
                
                // 로그 추가
                log.info("User logged in - ID: {}, Role: {}", employeeId, user.getRole());
                
                // 세션 쿠키 설정
                Cookie sessionCookie = new Cookie("JSESSIONID", newSession.getId());
                sessionCookie.setPath("/");
                sessionCookie.setHttpOnly(true);
                sessionCookie.setSecure(request.isSecure()); // HTTPS 사용시에만 전송
                sessionCookie.setMaxAge(-1); // 브라우저 종료시 삭제
                
                // SameSite 속성 설정 (크로스 사이트 요청 방지)
                String sameSite = "Lax";
                String header = String.format("JSESSIONID=%s; Path=/; HttpOnly; SameSite=%s", 
                    sessionCookie.getValue(), sameSite);
                response.setHeader("Set-Cookie", header);
                
                response.addCookie(sessionCookie);
                
                return "redirect:/notices"; // 대시보드로 리다이렉트 변경
            }
            
            log.warn("Login failed for user ID: {}", employeeId);
            return "redirect:/login?error";
            
        } catch (Exception e) {
            log.error("Error during login process: ", e);
            return "redirect:/login?error";
        }
    }
}