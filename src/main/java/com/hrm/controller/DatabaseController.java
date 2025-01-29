package com.hrm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.stereotype.Controller;

@Controller
public class DatabaseController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/db-status")
    @ResponseBody
    public String checkDatabaseConnection() {
        try {
            // 간단한 쿼리를 실행하여 DB 연결 상태 확인
            jdbcTemplate.execute("SELECT 1");
            return "<h1>Database Connection: SUCCESS</h1>";
        } catch (Exception e) {
            return "<h1>Database Connection: FAILED</h1><p>" + e.getMessage() + "</p>";
        }
    }
}
