package com.hrm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.hrm.dao")
@SpringBootApplication
@EnableScheduling
public class HrmProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(HrmProjectApplication.class, args);
	}

}
