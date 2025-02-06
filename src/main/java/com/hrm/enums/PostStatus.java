package com.hrm.enums;

public enum PostStatus {
	ACTIVE("진행중"), CLOSED("마감됨");

	private final String description; // "진행중", "마감됨" 한글을 저장하기 위한 문자열

	private PostStatus(String description) {
		this.description = description;
	}

	public String getStatus() {
		return description;
	}

}
