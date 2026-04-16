package com.example.domain.common;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * 入力された文字列がひらがなと長音符(ー)と全角スペースのみで構成されているか検証する.
 * 
 * このアノテーションはフィールドに付与する.
 * 
 */
@Target({ElementType.FIELD}) 
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SafeHiraganaValidator.class)
@Documented
public @interface SafeHiragana {
	
	String message() default "{SafeHiragana.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    
}
