package com.hrm.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hrm.dao.UserAccountDao;
import com.hrm.dto.UserAccountDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordService {
     
    private final UserAccountDao userAccountDao;// 이름도 일관성있게 변경
    private final PasswordEncoder passwordEncoder; 
    
    
    // 비밀번호 변경 
    @Transactional
    public boolean changePassword(String employeeId, String currentPassword, String newPassword) {
        log.info("비밀번호 변경 시도 - 사원번호: {}", employeeId);
        try {
            UserAccountDto user = userAccountDao.findByEmployeeId(employeeId);
            
            if (user == null) {
                log.info("사용자를 찾을 수 없음 - 사원번호: {}", employeeId);
                return false;
            }

            if (passwordEncoder.matches(currentPassword, user.getPassword())) {
                String encodedPassword = passwordEncoder.encode(newPassword);
                int result = userAccountDao.updatePassword(employeeId, encodedPassword);
                log.info("비밀번호 변경 완료 - 사원번호: {}, 결과: {}", employeeId, result > 0);
                return result > 0;
            }
            
            log.info("현재 비밀번호가 일치하지 않음 - 사원번호: {}", employeeId);
            return false;
        } catch (Exception e) {
            log.error("비밀번호 변경 중 오류 발생 - 사원번호: {}", employeeId, e);
            return false;
        }
    }
}