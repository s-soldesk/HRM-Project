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

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;
    private final EmployeeService employeeService;

    @GetMapping("/messages")
    public String getMessages(Model model, HttpSession session) {
        try {
            UserAccountDto loggedInUser = (UserAccountDto) session.getAttribute("loggedInUser");
            String userEmail = (String) session.getAttribute("loggedInEmail");
            
            log.info("Session info - loggedInUser: {}, email: {}", loggedInUser, userEmail);
            
            if (loggedInUser == null || userEmail == null) {
                log.warn("No user in session");
                return "redirect:/login";
            }

            try {
                EmployeeDto employee = employeeService.getEmployeeById(userEmail);
                log.info("Found employee: {}", employee);
                
                if (employee != null) {
                    model.addAttribute("currentUserId", employee.getEmployeeId());
                    List<EmployeeDto> employees = messageService.getAllEmployees();
                    log.info("Found {} employees", employees.size());
                    model.addAttribute("employees", employees);
                    
                    List<MessageDto> messages = messageService.getReceivedMessages(employee.getEmployeeId());
                    log.info("Found {} messages", messages.size());
                    model.addAttribute("messages", messages);
                } else {
                    log.warn("No employee found for email: {}", userEmail);
                    return "redirect:/login";
                }
            } catch (Exception e) {
                log.error("Error processing employee data: ", e);
                throw e;
            }
            
            return "message/list";
            
        } catch (Exception e) {
            log.error("Error in getMessages: ", e);
            throw new RuntimeException("Failed to load messages page", e);
        }
    }

    @GetMapping("/messages/chat/{userId}")
    public String chatRoom(@PathVariable("userId") Integer userId, Model model, HttpSession session) {
        try {
            String employeeId = (String) session.getAttribute("loggedInEmail");
            log.info("Opening chat room between {} and {}", employeeId, userId);
            
            if (employeeId == null) {
                return "redirect:/login";
            }

            EmployeeDto employee = employeeService.getEmployeeById(employeeId);
            
            if (employee != null) {
                model.addAttribute("currentUserId", employee.getEmployeeId());
                model.addAttribute("selectedUserId", userId);
                
                // 모든 직원 목록 가져오기
                List<EmployeeDto> employees = messageService.getAllEmployees();
                model.addAttribute("employees", employees);
                
                // 채팅 메시지 목록
                List<MessageDto> messages = messageService.getChatMessages(employee.getEmployeeId(), userId);
                model.addAttribute("messages", messages);
                
                log.info("Loaded chat room with {} messages", messages.size());
            }
            
            return "message/list";
            
        } catch (Exception e) {
            log.error("Error in chatRoom: ", e);
            return "error";
        }
    }

    @PostMapping("/messages/send")
    public String sendMessage(@ModelAttribute MessageDto message, HttpSession session) {
        try {
            Integer employeeId = (Integer) session.getAttribute("employeeId");
            log.info("Sending message from {} to {}", employeeId, message.getReceiverId());
            
            if (employeeId == null) {
                return "redirect:/login";
            }

            EmployeeDto employee = employeeService.getEmployeeById(String.valueOf(employeeId));
            
            if (employee == null || message.getReceiverId() == null) {
                log.error("Invalid sender or receiver");
                return "redirect:/messages";
            }
            
            // 메시지 정보 설정
            message.setSenderId(employee.getEmployeeId());
            message.setSentTime(LocalDateTime.now());
            message.setIsRead(false);
            
            // 메시지 전송
            messageService.sendMessage(message);
            log.info("Message sent successfully");
            
            return "redirect:/messages/chat/" + message.getReceiverId();
            
        } catch (Exception e) {
            log.error("Error in sendMessage: ", e);
            return "redirect:/messages";
        }
    }

    @GetMapping("/messages/{id}")
    public String viewMessage(@PathVariable("id") Integer messageId, Model model) {
        try {
            MessageDto message = messageService.getMessage(messageId);
            if (message != null) {
                messageService.markAsRead(messageId);
                model.addAttribute("message", message);
                log.info("Viewing message {}", messageId);
            }
            return "message/view";
            
        } catch (Exception e) {
            log.error("Error in viewMessage: ", e);
            return "error";
        }
    }

    @GetMapping("/messages/reply/{id}")
    public String replyForm(@PathVariable("id") Integer messageId, Model model) {
        try {
            MessageDto originalMessage = messageService.getMessage(messageId);
            if (originalMessage != null) {
                model.addAttribute("originalMessage", originalMessage);
                model.addAttribute("receiverId", originalMessage.getSenderId());
                log.info("Opening reply form for message {}", messageId);
            }
            return "message/form";
            
        } catch (Exception e) {
            log.error("Error in replyForm: ", e);
            return "error";
        }
    }
}