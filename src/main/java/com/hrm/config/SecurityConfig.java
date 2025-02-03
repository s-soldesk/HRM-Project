package com.hrm.config;

import com.hrm.service.UserDetailsServiceImpl;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@MapperScan(basePackages = "com.hrm.dao")
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/resources/**").permitAll() // 로그인 및 정적 리소스 허용
                .requestMatchers("/admin/**").hasRole("Admin")           // 관리자만 접근 가능
                .requestMatchers("/hr/**").hasAnyRole("HR", "Admin")    // 인사, 관리자만 접근 가능
                .requestMatchers("/employee/**").hasAnyRole("Employee", "HR", "Admin") // 사원, 인사, 관리자 접근 가능
                .anyRequest().authenticated()                           // 나머지는 인증 필요
            )
            .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("employeeId") // 사용자 정의 로그인 필드
                .passwordParameter("password")   // 비밀번호 필드
                .defaultSuccessUrl("/index", true) // 로그인 성공 시 이동 경로
                .failureUrl("/login?error=true")   // 로그인 실패 시 이동 경로
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")                       // 로그아웃 요청 URL
                .logoutSuccessUrl("/login?logout=true")     // 로그아웃 성공 시 이동 경로
                .invalidateHttpSession(true)                // 세션 무효화
                .clearAuthentication(true)                 // 인증 정보 삭제
                .deleteCookies("JSESSIONID")               // JSESSIONID 쿠키 삭제
                .permitAll()
            )
            .csrf(csrf -> csrf.disable());  // CSRF 비활성화 (API 요청에 필요할 경우)

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                   .userDetailsService(userDetailsService)
                   .passwordEncoder(passwordEncoder())
                   .and()
                   .build();
    }
}
