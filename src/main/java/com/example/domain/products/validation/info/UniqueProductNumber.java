package com.example.domain.products.validation.info;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * 入力された商品番号が m_product に登録されている商品番号と重複していないか検証するアノテーション.
 * 
 * このアノテーションはクラスに付与する.
 * productIdField に登録・修正する商品のIDのフィールド名を指定する.
 * productNumberField に登録・修正する商品の商品番号のフィールド名を指定する.
 * 
 * 例：
 * UniqueProductNumber(productIdField="productId",productNumberField="productNumber")
 * 
 */
@Target({ElementType.TYPE}) 
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueProductNumberValidator.class)
@Documented
public @interface UniqueProductNumber {
	
	String message() default "{UniqueProductNumber.message}";	// デフォルトエラーメッセージ.
    Class<?>[] groups() default {};							// groupsを指定できる仕組みの設定.
    Class<? extends Payload>[] payload() default {};
    
    String productIdField();		// この属性を設定することで,各formクラスのフィールド名をバリデータクラスに渡すことができる.
    String productNumberField();
}
