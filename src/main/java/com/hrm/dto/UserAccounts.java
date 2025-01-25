package com.hrm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAccounts {
    private int userId;
    private int employeeId;
    private String username;
    private String password;
    private String role;
}
