package com.example.domain.products.validation.info;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.domain.products.service.ProductInfoService;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

/**
 * UniqueProductNumber アノテーションの検証処理を行う.
 * 
 * null の場合は @NotBlank に任す.
 * 一致しない場合は productNumber フィールドにエラーを紐付ける.
 */
@Slf4j
public class UniqueProductNumberValidator implements ConstraintValidator<UniqueProductNumber, Object> {

	@Autowired
	private ProductInfoService productInfoService;

	private String message; // エラーメッセージ;

	private String productId;

	private String productNumber;

	/**
	 * UniqueProductNumber アノテーションの初期化処理を行う.
	 * 入力された商品番号,デフォルトのエラーメッセージをアノテーションから取得して保持する.
	 *
	 * @param annotation UniqueProductNumber アノテーションの属性値を持ったオブジェクト.
	 */
	@Override
	public void initialize(UniqueProductNumber annotation) {
		this.message = annotation.message();
		this.productId = annotation.productIdField();
		this.productNumber = annotation.productNumberField();

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
			Object productIdValue = beanWrapperImpl.getPropertyValue(productId);	// 引数に渡した文字列(確認したいformクラスのフィールド名)を探して値を取得する.
			Object productNumberValue = beanWrapperImpl.getPropertyValue(productNumber);

			if (productNumberValue == null) {

				return true;

			}

			/* 取得した商品の商品番号と商品IDから,商品番号が既存の商品番号と(修正時は自身の商品番号は除外して)重複しているか確認し,
			 * 重複している件数をcountに代入する.*/
			int count = productInfoService.getCountDuplicates(productIdValue, productNumberValue);

			// 重複している件数が0件なら重複はないためtrueになる(修正時なら自身の商品番号との重複は重複とみなさないため0件になり,trueになる).
			if (count == 0) {

				return true;

			} else {

				// エラーを特定のフィールドに紐づけて伝える
				context.disableDefaultConstraintViolation(); // デフォルトのクラスエラーを無効化.
				context.buildConstraintViolationWithTemplate(message) // デフォルトメッセージをメッセージに再設定する.
						.addPropertyNode("productNumber") // productNumberフィールドにエラーをつける.
						.addConstraintViolation(); // バリデーションエラーを確定する.

				return false;
			}

		} catch (BeansException e) {
			
			log.error("@UniqueProductNumberValidator バリデーション中に例外が発生しました。フィールド名が正しいか確認してください: {}", e.getMessage());

			return false;

		} catch (Exception e) {
			// その他予期せぬエラー（DB接続エラーなど）.
			log.error("@UniqueProductNumberValidator で予期せぬエラーが発生しました", e);
			return false;
		}
	}
}
