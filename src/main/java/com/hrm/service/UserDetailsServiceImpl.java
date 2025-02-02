package com.hrm.service;

import com.hrm.dao.UserAccountRepository;
import com.hrm.dto.UserAccountDto;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
    private UserAccountRepository userAccountRepository;

    @Override
    public UserDetails loadUserByUsername(String employeeId) throws UsernameNotFoundException {
        // 사용자 계정 정보 조회
        UserAccountDto user = userAccountRepository.findByEmployeeId(employeeId);

        // 사용자가 존재하지 않으면 예외 발생
        if (user == null) {
            throw new UsernameNotFoundException("User not found with employeeId: " + employeeId);
        }

        // Spring Security의 User 객체를 생성하여 반환
        return new User(
            String.valueOf(user.getEmployeeId()),  // 사용자 ID
            user.getPassword(),                    // 비밀번호
            Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getAuthority())) // 권한 설정
        );
    }
}
