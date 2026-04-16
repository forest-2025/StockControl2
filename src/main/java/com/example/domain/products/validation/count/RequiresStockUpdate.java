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
 * (StockEditForm の productId をもとに在庫数を取得して比較するため,
 * フィールドに付与すると在庫数を取得できないのでクラスに付与する).
 */
@Target({ElementType.TYPE}) 
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RequiresStockUpdateValidator.class)
@Documented
public @interface RequiresStockUpdate {

	String message() default "{RequiresStockUpdate.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
