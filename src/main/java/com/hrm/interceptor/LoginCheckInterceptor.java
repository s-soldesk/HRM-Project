package com.hrm.interceptor;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {

    private static final List<String> ALLOWED_PATHS = Arrays.asList(
        "/", "/login", "/logout", "/css/", "/js/", "/images/", "/uploads/"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        
        // 허용된 경로는 인터셉터를 통과
        if (isAllowedPath(requestURI)) {
            return true;
        }

        // 세션 체크
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedInEmail") == null) {
            log.info("미인증 사용자 요청: {}", requestURI);
            response.sendRedirect("/login?redirectURL=" + requestURI);
            return false;
        }

        return true;
    }

    private boolean isAllowedPath(String requestURI) {
        return ALLOWED_PATHS.stream()
                .anyMatch(path -> requestURI.startsWith(path));
    }
}