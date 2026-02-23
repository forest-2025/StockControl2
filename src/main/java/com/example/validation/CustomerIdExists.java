package com.example.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;


/**
 * 出荷先IDがm_customerに存在するか検証するアノテーション.
 * 
 * このアノテーションはフィールドに付与する.
 * 
 */
@Target({ElementType.FIELD}) 
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CustomerIdExistsValidator.class)
@Documented
public @interface CustomerIdExists {
	
	String message() default "選択された項目が見つかりません";	// デフォルトエラーメッセージ.
    Class<?>[] groups() default {};						// groupsを指定できる仕組みの設定.
    Class<? extends Payload>[] payload() default {};
    
}
