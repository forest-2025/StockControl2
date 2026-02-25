package com.example.domain.users.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.domain.users.service.UserService;

import lombok.extern.slf4j.Slf4j;

/**
 * UniqueUser アノテーションの検証処理を行う.
 * 
 * null の場合は @NotBlank に任す.
 * 一致しない場合は該当するフィールドにエラーを紐付ける.
 */
@Slf4j
public class UniqueUserValidator implements ConstraintValidator<UniqueUser, Object> {

	@Autowired
	private UserService userService;

	private String message; // デフォルトのエラーメッセージ;

	private String userIdField; // userIdField属性に設定したフィールド名.

	private String checkField; // checkField属性に設定したフィールド名.

	private String columnName; // columnName属性に設定したフィールドに対応するカラム名.

	/**
	 * UniqueUser アノテーションの初期化処理を行う.
	 * カラム名idに対応するフィールド名,重複を確認するフィールド名、
	 * 重複を確認するフィールドに対応するカラム名,デフォルトのエラーメッセージをアノテーションから取得して保持する.
	 *
	 * @param annotation UniqueProductNumber アノテーションの属性値を持ったオブジェクト.
	 */
	@Override
	public void initialize(UniqueUser annotation) {
		this.message = annotation.message();
		this.userIdField = annotation.userIdField();
		this.checkField = annotation.checkField();
		this.columnName = annotation.columnName();

	}

	/**
	 * 入力された商品番号がデータベースに登録されている m_product に登録されている商品番号と重複していないか確認する.
	 *
	 * @param value 検証対象のオブジェクト.
	 * @param context バリデーションコンテキスト.
	 * @return 重複がなければ true,重複があれば false.
	 */
	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {

		try {

			if (value == null) {

				return true;
			}

			BeanWrapperImpl beanWrapperImpl = new BeanWrapperImpl(value); // フォームで入力されたformクラスのオブジェクトをspringbootが操作しやすいようにラップする.
			Object userIdValue = beanWrapperImpl.getPropertyValue(userIdField); // 引数に渡した文字列(確認したいformクラスのフィールド名)を探して値を取得する.
			Object checkItemValue = beanWrapperImpl.getPropertyValue(checkField);

			// userIdValueは登録時はnullになるためnullチェックは行わない.
			if (checkItemValue == null) {

				return true;
			}

			// 重複チェック実行
			boolean isUnique = userService.isNotDuplicates(columnName, userIdValue, checkItemValue);

			if (!isUnique) {
				//  項目に応じたメッセージキーを選択
				String errorMessage = this.getErrorMessage(checkField);

				// エラーを特定のフィールドに紐付ける
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate(errorMessage)
						.addPropertyNode(checkField)
						.addConstraintViolation();

				return false;
			}

			return true;

		} catch (BeansException e) {

			log.error("@UniqueUser バリデーション中に例外が発生しました。フィールド名が正しいか確認してください: {}", e.getMessage());

			return false;

		} catch (Exception e) {
			// その他予期せぬエラー（DB接続エラーなど）.
			log.error("@UniqueUser で予期せぬエラーが発生しました", e);
			return false;
		}
	}

	/**
	 * 重複チェックを行うフィールドに対応するバリデーションエラーメッセージを validationMessages.properties から取得する.
	 * 
	 * @param checkField フィールド名.
	 * @return エラーメッセージ.
	 * */
	public String getErrorMessage(Object checkField) {
		
		 return switch (checkField) {
	        case String s when s.equals("employeeNumber") -> "{DuplicateEmployeeNumber}";
	        case String s when s.equals("emailAddress") -> "{DuplicateEmailAddress}";
	        default -> message;
	    };
		
	}

}
