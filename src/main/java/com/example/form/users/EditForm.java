package com.example.form.users;

import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EditForm {

	@NotBlank
	@Pattern(regexp = "^[A-Z][0-9]{5}$", message = "1文字目は半角英大文字、2文字目から半角数字で5桁入力してください(例: E12345など)")
	private String employeeNumber; // 従業員番号.

	@NotBlank
	@Size(min = 1, max = 100)
	private String familyName; // ユーザー姓.

	@NotBlank
	@Size(min = 1, max = 100)
	private String firstName; // ユーザー名.

	@NotBlank
	@Email
	@Size(max = 254)
	private String emailAddress; // メールアドレス.

	@NotNull(message="管理者権限は必ず選択してください")
	@Range(min = 0, max = 1)
	private Integer isAdmin; // 管理者権限.

}
