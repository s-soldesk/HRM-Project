package com.hrm.enums;

public enum Role {
    ADMIN, EMPLOYEE, HR;

    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}
