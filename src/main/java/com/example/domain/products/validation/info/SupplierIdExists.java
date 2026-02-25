package com.example.domain.products.validation.info;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * 入荷先IDが m_supplier に存在するか検証するアノテーション.
 * 
 * このアノテーションはフィールドに付与する.
 * 
 */
@Target({ElementType.FIELD}) 
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SupplierIdExistsValidator.class)
@Documented
public @interface SupplierIdExists {

	String message() default "{SupplierIdExists.message}";	// デフォルトエラーメッセージ.
    Class<?>[] groups() default {};					// groupsを指定できる仕組みの設定.
    Class<? extends Payload>[] payload() default {};

}
