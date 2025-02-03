package com.hrm.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = "com.hrm.mapper") // MyBatis Mapper 패키지 경로 설정
public class MyBatisConfig {
}
