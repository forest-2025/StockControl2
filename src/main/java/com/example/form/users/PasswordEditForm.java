package com.example.form.users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 *  パスワード修正フォーム画面の入力値を受け取るフォームクラス.
 *  
 */
@Data
public class PasswordEditForm {
	
	@NotBlank
	@Size(min = 5, max = 72)
	@Pattern(regexp = "^[!-~]+$", 
	message = "半角英数字と半角記号のなかから入力してください")
	private String password; // パスワード.
	
	private String reEnterPassword;	// パスワード再入力.
}