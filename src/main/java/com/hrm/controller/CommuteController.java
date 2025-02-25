package com.hrm.controller;

import com.hrm.dao.UserAccountDao;
import com.hrm.service.CommuteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/attendance/commute")
public class CommuteController {

    @Autowired
    private CommuteService commuteService;
    
    @Autowired
    private UserAccountDao userAccountDao;
    
    // 출근 기록 - AJAX 처리를 위해 수정
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/check_in")
    @ResponseBody
    public ResponseEntity<Map<String, String>> checkIn(@AuthenticationPrincipal UserDetails userDetails) {
        Map<String, String> response = new HashMap<>();
        
        // 로그인한 사용자의 이메일 가져오기 (UserAccounts 테이블의 EmployeeID 값)
        String employeeEmail = userDetails.getUsername();

        // 이메일을 이용해 Employee 테이블의 EmployeeID(Integer) 조회
        Integer employeeId = userAccountDao.findEmployeeIdByEmail(employeeEmail);
        
        if (employeeId == null) {
            response.put("message", "사원 정보를 찾을 수 없습니다.");
            return ResponseEntity.badRequest().body(response);
        }
        
        boolean isCheckInSuccessful = commuteService.recordCheckIn(employeeId);
        String message = isCheckInSuccessful ? "출근이 완료되었습니다." : "이미 출근 기록이 존재합니다.";
        response.put("message", message);
        
        return isCheckInSuccessful ? 
            ResponseEntity.ok(response) : 
            ResponseEntity.badRequest().body(response);
    }

    // 퇴근 기록 - AJAX 처리를 위해 수정
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/check_out")
    @ResponseBody
    public ResponseEntity<Map<String, String>> checkOut(@AuthenticationPrincipal UserDetails userDetails) {
        Map<String, String> response = new HashMap<>();
        
        // 로그인한 사용자의 이메일 가져오기 (UserAccounts 테이블의 EmployeeID 값)
        String employeeEmail = userDetails.getUsername();

        // 이메일을 이용해 Employee 테이블의 EmployeeID(Integer) 조회
        Integer employeeId = userAccountDao.findEmployeeIdByEmail(employeeEmail);
        
        if (employeeId == null) {
            response.put("message", "사원 정보를 찾을 수 없습니다.");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (commuteService.hasCheckOutRecord(employeeId)) {
            response.put("message", "이미 퇴근 기록이 존재합니다.");
            return ResponseEntity.badRequest().body(response);
        }

        boolean isCheckOutSuccessful = commuteService.recordCheckOut(employeeId);
        String message = isCheckOutSuccessful ? "퇴근이 완료되었습니다." : "출근 기록이 없습니다. 퇴근 기록을 추가할 수 없습니다.";
        response.put("message", message);
        
        return isCheckOutSuccessful ? 
            ResponseEntity.ok(response) : 
            ResponseEntity.badRequest().body(response);
    }
}