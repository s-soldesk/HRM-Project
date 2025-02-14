package com.hrm.enums;

public enum Position {
	INTERN("인턴"),
	STAFF("사원"), 
	SENIOR("주임"),
	ASSISTANT_MANAGER("대리"),
	MANAGER("과장"), 
	SENIOR_MANAGER("차장"),
	DEPUTY_GENERAL_MANAGER("부장");

	private final String position;

	Position(String position) {
		this.position = position;
	}

	public String getPosition() {
		return this.position;
	}
}
