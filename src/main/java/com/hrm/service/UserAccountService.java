package com.hrm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hrm.dto.UserAccountDto;
import com.hrm.mapper.UserAccountsMapper;
import com.hrm.repository.UserAccountRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAccountService {
	   
    private final UserAccountsMapper userAccountsMapper;

    public UserAccountDto findByEmployeeId(Integer employeeId) {
        log.info("사용자 조회 시도 - 사원번호: {}", employeeId);
        try {
            UserAccountDto user = userAccountsMapper.findByEmployeeId(employeeId);
            if (user != null) {
                log.info("사용자 조회 성공 - 사원번호: {}", employeeId);
            } else {
                log.info("사용자 없음 - 사원번호: {}", employeeId);
            }
            return user;
        } catch (Exception e) {
            log.error("사용자 조회 중 오류 발생 - 사원번호: {}", employeeId, e);
            return null;
        }
    }

    public UserAccountDto login(Integer employeeId, String password) {
        log.info("로그인 시도 - 사원번호: {}", employeeId);
        try {
            UserAccountDto user = findByEmployeeId(employeeId);
            if (user == null) {
                log.info("사용자를 찾을 수 없음 - 사원번호: {}", employeeId);
                return null;
            }

            if (password.equals(user.getPassword())) {
                log.info("로그인 성공 - 사원번호: {}", employeeId);
                return user;
            }

            log.info("비밀번호 불일치 - 사원번호: {}", employeeId);
            return null;

        } catch (Exception e) {
            log.error("로그인 처리 중 오류 발생 - 사원번호: {}", employeeId, e);
            return null;
        }
    }

    @Transactional
    public boolean changePassword(Integer employeeId, String currentPassword, String newPassword) {
        log.info("비밀번호 변경 시도 - 사원번호: {}", employeeId);
        try {
            UserAccountDto user = userAccountsMapper.findByEmployeeId(employeeId);
            
            if (user == null) {
                log.info("사용자를 찾을 수 없음 - 사원번호: {}", employeeId);
                return false;
            }

            if (currentPassword.equals(user.getPassword())) {
                int result = userAccountsMapper.updatePassword(employeeId, newPassword);
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