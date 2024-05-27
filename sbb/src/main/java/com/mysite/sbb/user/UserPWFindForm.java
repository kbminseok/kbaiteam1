package com.mysite.sbb.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPWFindForm {
	@Size(min = 3, max = 25)
	@NotBlank(message = "사용자ID는 필수항목입니다.")
	private String username;

	@NotBlank(message = "이메일은 필수항목입니다.")
	@Email
	private String email;
}