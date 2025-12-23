package com.example.form.users;

import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterForm {

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

	/* パスワードをBCryptPasswordEncoderでハッシュ化するとき72バイト(半角英数字は1文字１バイト,
	 * 全角ひらがな・カタカナ(全角・半角ともに)・漢字は3バイト(難しい漢字は4バイト))を超すと切り捨てられていた.
	 * 例)aが72個のパスワードとaが100個のパスワードとaが72個+bの73文字のパスワードは,72バイトめまで同じなのですべておなじパスワードとみなされる.
	 * そのため長いパスワードを許容するならセキュリティ的にBCryptPasswordEncoderは使用しないほうがいい(Argon2とかのほうがいい).
	 * いままでは72バイトを越えると超過分は勝手に切り捨てられていたがspringsecurity6から,java.lang.IllegalArgumentException,
	 * (Javaのメソッドに不正な値や期待されていない値（引数）が渡されたときにスローされる実行時例外)がでてエラーになるので使用する際は注意する.*/
	@NotBlank
	@Size(min = 5, max = 72, message = "パスワードは5文字以上72文字以下で入力してください")
	@Pattern(regexp = "^[!-~]+$", 
	message = "半角英数字と半角記号のなかから入力してください")
	private String password; // パスワード.

	@NotNull(message="管理者権限は必ず選択してください")
	@Range(min = 0, max = 1)
	private Integer isAdmin; // 管理者権限.

}
