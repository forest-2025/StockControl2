package com.example.domain.products.validation.count;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.domain.products.service.ProductCountService;
import com.example.form.products.count.StockEditForm;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * RequiresStockUpdate アノテーションの検証処理を行う.
 * 
 * null の場合は @NotNull に任す.
 * 一致しない場合は actualProductCount フィールドにエラーを紐付ける.
 */
public class RequiresStockUpdateValidator implements ConstraintValidator<RequiresStockUpdate, StockEditForm> {

	@Autowired
	private ProductCountService productCountService;

	private String message;	// エラーメッセージ.

	
	/**
	 * RequiresStockUpdate アノテーションの初期化処理を行う.
	 * デフォルトのエラーメッセージをアノテーションから取得して保持する.
	 *
	 * @param annotation RequiresStockUpdate アノテーションの属性値を持ったオブジェクト.
	 */
	@Override
	public void initialize(RequiresStockUpdate annotation) {
		this.message = annotation.message();
	}

	
	/**
	 * 在庫数を取得して実在庫数とおなじ数量でないかを確認する.
	 *
	 * @param form 検証対象のオブジェクト.
	 * @param context バリデーションコンテキスト.
	 * @return 実在庫数と在庫数が同数でないなら true,同数なら false.
	 */
	@Override
	public boolean isValid(StockEditForm form, ConstraintValidatorContext context) {

		if (form == null || form.getProductId() == null || form.getActualProductCount() == null) {

			return true;
		}

		// 商品の在庫数を取得する.
		Integer stockQuantity = productCountService.getOneStockQuantity(form.getProductId());

		// 在庫数と実在庫数が同数でないかを確認する.
		if (stockQuantity == form.getActualProductCount()) {
			context.buildConstraintViolationWithTemplate(message) // デフォルトメッセージをメッセージに再設定する.
					.addPropertyNode("actualProductCount") // actualProductCountフィールドにエラーをつける.
					.addConstraintViolation();
			return false;
		}
		
		return true;
	}

}
