package com.example.domain.products.validation.count;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * 実在庫数が在庫数とおなじ数量でないかを検証するアノテーション.
 * 
 * このアノテーションはクラスに付与する.
 * 
 */
@Target({ElementType.TYPE}) 
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RequiresStockUpdateValidator.class)
@Documented
public @interface RequiresStockUpdate {

	String message() default "この商品の在庫の全数量を入力してください (在庫数と同じ数量を入力しないでください)";	// デフォルトエラーメッセージ.
    Class<?>[] groups() default {};					// groupsを指定できる仕組みの設定.
    Class<? extends Payload>[] payload() default {};
}
