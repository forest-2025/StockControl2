package com.example.form.users;

import org.hibernate.validator.constraints.Range;

import com.example.validation.ValidGroup1;
import com.example.validation.ValidGroup2;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 *  ユーザー情報修正フォーム画面の入力値を受け取るフォームクラス.
 *  
 */
@Data
public class EditForm {

	@NotBlank(groups = ValidGroup1.class)
	@Pattern(regexp = "^[A-Z][0-9]{5}$", groups = ValidGroup2.class)
	private String employeeNumber; // 従業員番号.

	@NotBlank(groups = ValidGroup1.class)
	@Size(min = 1, max = 100, groups = ValidGroup2.class)
	private String familyName; // ユーザー姓.

	@NotBlank(groups = ValidGroup1.class)
	@Size(min = 1, max = 100, groups = ValidGroup2.class)
	private String firstName; // ユーザー名.

	@NotBlank(groups = ValidGroup1.class)
	@Email(groups = ValidGroup2.class)
	@Size(max = 254, groups = ValidGroup2.class)
	private String emailAddress; // メールアドレス.

	@NotNull(groups = ValidGroup1.class)
	@Range(min = 0, max = 1, groups = ValidGroup2.class)
	private Integer isAdmin; // 管理者権限.

}
