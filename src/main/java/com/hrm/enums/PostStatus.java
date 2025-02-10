package com.hrm.enums;

public enum PostStatus {
	ACTIVE("모집중"), CLOSED("마감됨");

	private final String status; // "진행중", "마감됨" 한글을 저장하기 위한 문자열

	private PostStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

}
