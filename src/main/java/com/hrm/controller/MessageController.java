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
            Integer employeeId = (Integer) session.getAttribute("loggedInEmail");
            
            if (employeeId == null) {
                log.warn("No employeeId found in session");
                return "redirect:/login";
            }

            try {
                EmployeeDto employee = employeeService.getEmployeeById(employeeId); // 수정된 부분
                
                if (employee != null) {
                    model.addAttribute("currentUserId", employeeId);
                    List<EmployeeDto> employees = messageService.getAllEmployees();
                    model.addAttribute("employees", employees);
                    
                    List<MessageDto> messages = messageService.getReceivedMessages(employeeId);
                    model.addAttribute("messages", messages);
                } else {
                    log.warn("No employee found for id: {}", employeeId);
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
            Integer employeeId = (Integer) session.getAttribute("loggedInEmail");
            
            if (employeeId == null) {
                return "redirect:/login";
            }

            EmployeeDto employee = employeeService.getEmployeeById(employeeId); // 수정된 부분
            
            if (employee != null) {
                model.addAttribute("currentUserId", employeeId);
                model.addAttribute("selectedUserId", userId);
                
                List<EmployeeDto> employees = messageService.getAllEmployees();
                model.addAttribute("employees", employees);
                
                List<MessageDto> messages = messageService.getChatMessages(employeeId, userId);
                model.addAttribute("messages", messages);
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
            Integer employeeId = (Integer) session.getAttribute("loggedInEmail");
            
            if (employeeId == null) {
                return "redirect:/login";
            }

            EmployeeDto employee = employeeService.getEmployeeById(employeeId); // 수정된 부분
            
            if (employee == null || message.getReceiverId() == null) {
                log.error("Invalid sender or receiver");
                return "redirect:/messages";
            }
            
            message.setSenderId(employeeId);
            message.setSentTime(LocalDateTime.now());
            message.setIsRead(false);
            
            messageService.sendMessage(message);
            log.info("Message sent successfully from {} to {}", employeeId, message.getReceiverId());
            
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
            }
            return "message/form";
            
        } catch (Exception e) {
            log.error("Error in replyForm: ", e);
            return "error";
        }
    }
}