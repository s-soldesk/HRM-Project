package com.hrm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	    @Bean
	    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	        http
	            .authorizeHttpRequests(authorize -> authorize
	            		.requestMatchers("/admin/**").hasRole("ADMIN")
	                    .requestMatchers("/manager/**").hasAnyRole("ADMIN", "MANAGER")
	                    .requestMatchers("/employee/**").hasRole("EMPLOYEE")
	                    .requestMatchers("/hr/**").hasAnyRole("hr", "ADMIN")
	                    .anyRequest().permitAll()
	            )
	            .csrf(csrf -> csrf.disable())
	            .sessionManagement(session -> session
	                .maximumSessions(1)  // 한 계정당 하나의 세션만 허용
	                .maxSessionsPreventsLogin(true)  // 동시 로그인 차단
	                .expiredUrl("/login?expired"))
	            .sessionManagement(session -> session
	                .sessionFixation().changeSessionId()  // 세션 ID 변경
	                .invalidSessionUrl("/login")  // 세션이 유효하지 않을 때 이동할 URL
	                .sessionConcurrency(sessionConcurrency -> sessionConcurrency
	                    .maximumSessions(1)
	                    .expiredUrl("/login?expired")));
	        
	        return http.build();
	    }
	}