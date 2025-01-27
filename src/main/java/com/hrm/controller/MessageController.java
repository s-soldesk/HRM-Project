package com.hrm.controller;

import java.time.LocalDateTime;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.hrm.dto.EmployeeDto;
import com.hrm.dto.MessageDto;
import com.hrm.service.EmployeeService;
import com.hrm.service.MessageService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MessageController {
   private final MessageService messageService;
   private final EmployeeService employeeService;  // 추가

   @GetMapping("/messages") 
   public String getMessages(Model model, HttpSession session) {
       String loggedInEmail = (String) session.getAttribute("loggedInEmail");
       EmployeeDto employee = employeeService.getEmployeeById(loggedInEmail);
       
       if (employee != null) {
           Integer employeeId = employee.getEmployeeId();
           model.addAttribute("receivedMessages", messageService.getReceivedMessages(employeeId));
           model.addAttribute("employees", messageService.getAllEmployees());
           model.addAttribute("currentUserId", employeeId);
       }
       return "message/list";
   }

   @PostMapping("/messages/send")
   public String sendMessage(@ModelAttribute MessageDto message, HttpSession session) {
       String loggedInEmail = (String) session.getAttribute("loggedInEmail");
       EmployeeDto employee = employeeService.getEmployeeById(loggedInEmail);
       
       if (employee != null) {
           message.setSenderId(employee.getEmployeeId());
           message.setSentTime(LocalDateTime.now());  // 시간 설정 추가
           message.setIsRead(false);  // 읽음 상태 초기화
           messageService.sendMessage(message);
       }
       return "redirect:/messages";
   }

   @GetMapping("/messages/{id}")
   public String viewMessage(@PathVariable("id") Integer messageId, Model model) {
       MessageDto message = messageService.getMessage(messageId);
       if (message != null) {
           messageService.markAsRead(messageId);
           model.addAttribute("message", message);
       }
       return "message/view";
   }

   @GetMapping("/messages/reply/{id}")
   public String replyForm(@PathVariable("id") Integer messageId, Model model) {
       MessageDto originalMessage = messageService.getMessage(messageId);
       if (originalMessage != null) {
           model.addAttribute("originalMessage", originalMessage);
           model.addAttribute("receiverId", originalMessage.getSenderId());  // 답장 수신자 설정
       }
       return "message/reply";
   }
}