package com.example.form.users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordEditForm {
	
	/* パスワードをBCryptPasswordEncoderでハッシュ化するとき72バイト(半角英数字は1文字１バイト,
	 * 全角ひらがな・カタカナ(全角・半角ともに)・漢字は3バイト(難しい漢字は4バイト))を超すと切り捨てられていた.
	 * 例)aが72個のパスワードとaが100個のパスワードとaが72個+bの73文字のパスワードは,72バイトめまで同じなのですべておなじパスワードとみなされる.
	 * そのため長いパスワードを許容するならセキュリティ的にBCryptPasswordEncoderは使用しないほうがいい(Argon2とかのほうがいい).
	 * いままでは72バイトを越えると超過分は勝手に切り捨てられていたがspringsecurity6から,java.lang.IllegalArgumentException,
	 * (Javaのメソッドに不正な値や期待されていない値（引数）が渡されたときにスローされる実行時例外)がでてエラーになるので使用する際は注意する.*/
	@NotBlank
	@Size(min = 5, max = 72)
	@Pattern(regexp = "^[!-~]+$", 
	message = "半角英数字と半角記号のなかから入力してください")
	private String password; // パスワード.

}
