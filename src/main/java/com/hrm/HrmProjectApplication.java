package com.hrm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.hrm.dao")
@SpringBootApplication
public class HrmProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(HrmProjectApplication.class, args);
	}

}
