package com.hrm.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

   @Override
   public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       // 임시로 하드코딩된 사용자 정보 반환
       CustomUserDetails userDetails = new CustomUserDetails();
       userDetails.setEmployeeId(1); // 테스트용 ID
       return userDetails;
   }
}