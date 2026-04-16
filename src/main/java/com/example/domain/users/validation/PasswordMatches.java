package com.example.domain.users.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * パスワードと確認用パスワードの値が一致するかどうかを検証するアノテーション.
 * 
 * このアノテーションはクラスに付与する.
 * password に比較元フィールド名(登録・修正するパスワードのフィールド名)を指定する.
 * reEnterPassword に比較先フィールド名(確認用パスワードのフィールド名)を指定する.
 * 
 * 例：
 * PasswordMatches(password="password", reEnterPassword="reEnterPassword")
 * 
 */
@Target({ElementType.TYPE}) 
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatchesValidator.class)
@Documented
public @interface PasswordMatches {
	
	    String message() default "{PasswordMatches.message}";	
	    Class<?>[] groups() default {};
	    Class<? extends Payload>[] payload() default {};
	    
	    String password(); 
	    String reEnterPassword(); 
	    
}
