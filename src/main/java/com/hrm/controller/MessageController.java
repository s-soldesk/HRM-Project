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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;
    private final EmployeeService employeeService;

    private Integer getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return Integer.valueOf(auth.getName());
        }
        throw new RuntimeException("No authenticated user found");
    }

    @GetMapping("/messages")
    public String getMessages(Model model) {
        try {
            Integer currentUserId = getCurrentUserId();
            
            EmployeeDto employee = employeeService.getEmployeeById(currentUserId);
            
            if (employee != null) {
                model.addAttribute("currentUserId", currentUserId);
                List<EmployeeDto> employees = messageService.getAllEmployees();
                model.addAttribute("employees", employees);
                
                List<MessageDto> messages = messageService.getReceivedMessages(currentUserId);
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
            Integer currentUserId = getCurrentUserId();
            
            EmployeeDto employee = employeeService.getEmployeeById(currentUserId);
            
            if (employee != null) {
                model.addAttribute("currentUserId", currentUserId);
                model.addAttribute("selectedUserId", userId);
                
                List<EmployeeDto> employees = messageService.getAllEmployees();
                model.addAttribute("employees", employees);
                
                List<MessageDto> messages = messageService.getChatMessages(currentUserId, userId);
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
            Integer currentUserId = getCurrentUserId();
            
            if (message.getReceiverId() == null) {
                log.error("Invalid receiver");
                return "redirect:/messages";
            }
            
            message.setSenderId(currentUserId);
            message.setSentTime(LocalDateTime.now());
            message.setIsRead(false);
            
            messageService.sendMessage(message);
            log.info("Message sent successfully from {} to {}", currentUserId, message.getReceiverId());
            
            return "redirect:/messages/chat/" + message.getReceiverId();
            
        } catch (Exception e) {
            log.error("Error in sendMessage: ", e);
            return "redirect:/messages";
        }
    }
}