package com.example.domain.products.validation.count;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.domain.products.service.ProductCountService;
import com.example.form.products.count.ShipForm;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * WithinStock アノテーションの検証処理を行う.
 * 
 * null の場合は @NotNull に任す.
 * 一致しない場合は amountOfChange フィールドにエラーを紐付ける.
 */
public class WithinStockValidator implements ConstraintValidator<WithinStock, ShipForm> {

	@Autowired
	private ProductCountService productCountService;

	private String message;	// エラーメッセージ.

	/**
	 * WithinStock アノテーションの初期化処理を行う.
	 * デフォルトのエラーメッセージをアノテーションから取得して保持する.
	 *
	 * @param annotation WithinStock アノテーションの属性値を持ったオブジェクト.
	 */
	@Override
	public void initialize(WithinStock annotation) {
		this.message = annotation.message();
	}

	/**
	 * 在庫数を取得して出荷数と比較し,出荷数が在庫数を越えていないか確認する.
	 *
	 * @param form 検証対象のオブジェクト.
	 * @param context バリデーションコンテキスト.
	 * @return 出荷数が在庫数を越えていなければ true,越えていれば false.
	 */
	@Override
	public boolean isValid(ShipForm form, ConstraintValidatorContext context) {

		if (form == null || form.getProductId() == null || form.getAmountOfChange() == null) {

			return true;
		}

		// 在庫数を取得する.
		Integer stockQuantity = productCountService.getOneStockQuantity(form.getProductId());

		// 出荷数が在庫数を越えていないか確認する.
		if (stockQuantity < form.getAmountOfChange()) {
			context.disableDefaultConstraintViolation(); // デフォルトのクラスエラーを無効化.
			context.buildConstraintViolationWithTemplate(message) // デフォルトメッセージをメッセージに再設定する.
					.addPropertyNode("amountOfChange") // amountOfChangeフィールドにエラーをつける.
					.addConstraintViolation();
			return false;
		}

		return true;
	}

}
