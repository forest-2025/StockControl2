package com.example.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.example.form.users.PasswordEditForm;

public class PasswordMatchValidator implements  ConstraintValidator<PasswordMatch, PasswordEditForm> {
	private String message; // アノテーションのメッセージを保存

    @Override
    public void initialize(PasswordMatch annotation) {
        this.message = annotation.message(); // ← デフォルト or 上書きされた値
    }
	@Override
	public boolean isValid(PasswordEditForm value, ConstraintValidatorContext context) {
		
	    if (value.getPassword() == null || value.getReEnterPassword() == null) {
	        // エラーを特定のフィールドに紐づけて伝える
	        context.disableDefaultConstraintViolation(); // デフォルトのクラスエラーを無効化.
	        context.buildConstraintViolationWithTemplate(message)
	               .addPropertyNode("password") // passwordフィールドにエラーをつける.
	               .addConstraintViolation();	// バリデーションエラーを確定する.

	        return false; // バリデーション失敗
	    }

	    // OKなら true を返す
	    return true;
	}

}
/* nullかどうかは@NotBlankがみるのでtrueを返す.
 * value.getPassword().equals(value.getReEnterPassword());で,
 * 二つのパスワードが一緒かどうか確認した結果(true/false)を返す. falseならバリデーションエラーになる. */
