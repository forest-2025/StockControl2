package com.example.domain.products.validation.count;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;


/**
 * 出荷先IDが m_customer に存在するか検証するアノテーション.
 * 
 * このアノテーションはフィールドに付与する.
 * 
 */
@Target({ElementType.FIELD}) 
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CustomerIdExistsValidator.class)
@Documented
public @interface CustomerIdExists {
	
	String message() default "{CustomerIdExists.message}";	// デフォルトエラーメッセージ.
    Class<?>[] groups() default {};					// groupsを指定できる仕組みの設定.
    Class<? extends Payload>[] payload() default {};
    
}
