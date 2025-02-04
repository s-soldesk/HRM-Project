package com.hrm.enums;

public enum Role {
    Admin, Employee, HR;

    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}
