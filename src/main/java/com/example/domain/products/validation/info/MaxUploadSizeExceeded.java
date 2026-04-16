package com.example.domain.products.validation.info;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * 画像ファイルが20MBを越えていないか検証するアノテーション.
 * 
 * このアノテーションはフィールドに付与する.
 * 
 */
@Target({ElementType.FIELD}) 
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MaxUploadSizeExceededValidator.class)
@Documented
public @interface MaxUploadSizeExceeded {
	
	String message() default "{OverSize.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};


}
