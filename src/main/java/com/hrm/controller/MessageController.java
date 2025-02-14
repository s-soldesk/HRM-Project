package com.hrm.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.hrm.dto.EmployeeDto;
import com.hrm.dto.MessageDto;
import com.hrm.dto.UserAccountDto;
import com.hrm.service.EmployeeService;
import com.hrm.service.MessageService;
import com.hrm.service.ProfileService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.hrm.dao.ProfileDao;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;
    private final EmployeeService employeeService;

    // 현재 로그인한 사용자의 이메일을 가져오는 메서드
    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return auth.getName();
        }
        throw new RuntimeException("No authenticated user found");
    }

    @GetMapping("/messages")
    public String getMessages(Model model) {
        try {
            String currentUserEmail = getCurrentUserId();
            
            EmployeeDto employee = messageService.getEmployeeByEmail(currentUserEmail);
            
            if (employee != null) {
                model.addAttribute("currentUserId", employee.getEmployeeId());
                List<EmployeeDto> employees = messageService.getAllEmployees();
                model.addAttribute("employees", employees);
                
                List<MessageDto> messages = messageService.getReceivedMessages(employee.getEmployeeId());
                model.addAttribute("messages", messages);
            }
            
            return "message/list";
            
        } catch (Exception e) {
            log.error("Error in getMessages: ", e);
            throw new RuntimeException("Failed to load messages page", e);
        }
    }

    @GetMapping("/messages/chat/{userId}")
    public String chatRoom(@PathVariable("userId") Integer userId, Model model) {
        try {
            String currentUserEmail = getCurrentUserId();
            EmployeeDto currentEmployee = messageService.getEmployeeByEmail(currentUserEmail);
            
            if (currentEmployee != null) {
                model.addAttribute("currentUserId", currentEmployee.getEmployeeId());
                model.addAttribute("selectedUserId", userId);
                
                List<EmployeeDto> employees = messageService.getAllEmployees();
                model.addAttribute("employees", employees);
                
                List<MessageDto> messages = messageService.getChatMessages(currentEmployee.getEmployeeId(), userId);
                model.addAttribute("messages", messages);
            }
            
            return "message/list";
            
        } catch (Exception e) {
            log.error("Error in chatRoom: ", e);
            return "error";
        }
    }

    @PostMapping("/messages/send")
    public String sendMessage(@ModelAttribute MessageDto message) {
        try {
            String currentUserEmail = getCurrentUserId();
            EmployeeDto currentEmployee = messageService.getEmployeeByEmail(currentUserEmail);
            
            if (message.getReceiverId() == null) {
                log.error("Invalid receiver");
                return "redirect:/messages";
            }
            
            message.setSenderId(currentEmployee.getEmployeeId());
            message.setSentTime(LocalDateTime.now());
            message.setIsRead(false);
            
            messageService.sendMessage(message);
            log.info("Message sent successfully from {} to {}", 
                     currentEmployee.getEmployeeId(), message.getReceiverId());
            
            return "redirect:/messages/chat/" + message.getReceiverId();
            
        } catch (Exception e) {
            log.error("Error in sendMessage: ", e);
            return "redirect:/messages";
        }
    }
}