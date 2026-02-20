package com.example.form.users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import com.example.validation.PasswordMatch;
import com.example.validation.ValidGroup1;
import com.example.validation.ValidGroup2;
import com.example.validation.ValidGroup3;

import lombok.Data;

/**
 *  パスワード修正フォーム画面の入力値を受け取るフォームクラス.
 *  
 */
@Data
@PasswordMatch(groups = ValidGroup3.class)
public class PasswordEditForm {

	@NotBlank(groups = ValidGroup1.class)
	@Size(min = 5, max = 72, groups = ValidGroup2.class)
	@Pattern(regexp = "^[!-~]+$", groups = ValidGroup2.class)
	private String password; // パスワード.

	private String reEnterPassword; // パスワード再入力.
}