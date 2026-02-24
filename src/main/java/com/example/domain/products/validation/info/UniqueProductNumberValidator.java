package com.example.domain.products.validation.info;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.domain.products.service.ProductInfoService;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

/**
 * PasswordMatches アノテーションの検証処理を行う.
 * 
 * null の場合は @NotBlank に任す.
 * 一致しない場合は password フィールドにエラーを紐付ける.
 */
@Slf4j
public class UniqueProductNumberValidator implements ConstraintValidator<UniqueProductNumber, Object> {

	@Autowired
	private ProductInfoService productInfoService;

	private String message; // エラーメッセージ;

	private String productNumber;

	/**
	 * UniqueProductNumber アノテーションの初期化処理を行う.
	 * 入力されたパスワードと確認用パスワード,デフォルトのエラーメッセージをアノテーションから取得して保持する.
	 *
	 * @param annotation UniqueProductNumber アノテーションの属性値を持ったオブジェクト.
	 */
	@Override
	public void initialize(UniqueProductNumber annotation) {
		this.message = annotation.message();
		this.productNumber = annotation.productNumber();

	}

	/**
	 * パスワードと確認用パスワードが一致するか確認する.
	 *
	 * @param value 検証対象のオブジェクト.
	 * @param context バリデーションコンテキスト.
	 * @return フィールドが一致する場合は true,一致しない場合は false.
	 */
	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {

		if (value == null) {
			return true;
		}

		try {
			BeanWrapperImpl beanWrapperImpl = new BeanWrapperImpl(value);	// 入力されたformクラスのオブジェクトをspringbootが操作しやすいようにラップ.
			Object productNumberValue = beanWrapperImpl.getPropertyValue(productNumber);	// 引数に渡した文字列(確認したいformクラスのフィールド名)を探して値を取得する.

			if (productNumberValue == null) {

				return true;

			} else {
				
				boolean isNotDuplicate = productInfoService.isNotDuplicateProductNumber(productNumber);
				
				if (isNotDuplicate) {	// 重複がないとtrue;
					
					return true;
					
				} else {

					// エラーを特定のフィールドに紐づけて伝える
					context.disableDefaultConstraintViolation(); // デフォルトのクラスエラーを無効化.
					context.buildConstraintViolationWithTemplate(message) // デフォルトメッセージをメッセージに再設定する.
							.addPropertyNode("productNumber") // productNumberフィールドにエラーをつける.
							.addConstraintViolation(); // バリデーションエラーを確定する.

					return false;
				}
			}
		} catch (Exception e) {

			log.error("@UniqueProductNumber バリデーション中に例外が発生しました", e);
			return false;
		}
	}
}
