package com.example.domain.products.validation.count;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * 出荷数が在庫数を越えていないか検証するアノテーション.
 * 
 * このアノテーションはクラスに付与する.
 * (ShipForm の productId をもとに在庫数を取得して比較するため,
 * フィールドに付与すると在庫数を取得できないのでクラスに付与する).
 * 
 */
@Target({ElementType.TYPE}) 
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = WithinStockValidator.class)
@Documented
public @interface WithinStock {
	
	String message() default "{WithinStock.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
   
}
