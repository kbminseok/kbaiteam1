package com.mysite.sbb.question;

import lombok.Getter;

@Getter
public enum QuestionEnum {
	QNA(0),
	FREE(1),
	BUG(2);

	private int status;

	QuestionEnum(int status) {
		this.status = status;
	}
}