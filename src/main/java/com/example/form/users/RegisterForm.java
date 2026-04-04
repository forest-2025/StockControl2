package com.example.form.users;

import com.example.domain.users.validation.PasswordMatches;
import com.example.domain.users.validation.UniqueUser;
import com.example.validation.ValidGroup1;
import com.example.validation.ValidGroup2;
import com.example.validation.ValidGroup3;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 *  ユーザー登録フォーム画面の入力値を受け取るフォームクラス.
 *  
 */
@Data
@PasswordMatches(password = "password",reEnterPassword = "reEnterPassword",groups = ValidGroup3.class)
@UniqueUser(userIdField="userId",checkField="employeeNumber",columnName="employee_number",groups = ValidGroup3.class)
@UniqueUser(userIdField="userId",checkField="emailAddress",columnName="email_address",groups = ValidGroup3.class)
public class RegisterForm {
	
	private Integer userId;

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

	@NotBlank(groups = ValidGroup1.class)
	@Size(min = 5, max = 72, groups = ValidGroup2.class)
	@Pattern(regexp = "^[!-~]+$", groups = ValidGroup2.class)
	private String password; // パスワード.
	
	private String reEnterPassword; // パスワード再入力.

	@NotNull(groups = ValidGroup1.class)
	//@Range(min = 0, max = 1, groups = ValidGroup2.class)
	//@AssertTrue(groups = ValidGroup1.class)
	private Boolean role; // 管理者権限.

}

/* パスワードをBCryptPasswordEncoderでハッシュ化するとき72バイト(半角英数字は1文字１バイト,
 * 全角ひらがな・カタカナ(全角・半角ともに)・漢字は3バイト(難しい漢字は4バイト))を超すと切り捨てられていた.
 * 例)aが72個のパスワードとaが100個のパスワードとaが72個+bの73文字のパスワードは,72バイトめまで同じなのですべておなじパスワードとみなされる.
 * そのため長いパスワードを許容するならセキュリティ的にBCryptPasswordEncoderは使用しないほうがいい(Argon2とかのほうがいい).
 * いままでは72バイトを越えると超過分は勝手に切り捨てられていたがspringsecurity6から,java.lang.IllegalArgumentException,
 * (Javaのメソッドに不正な値や期待されていない値（引数）が渡されたときにスローされる実行時例外)がでてエラーになるので使用する際は注意する.*/
