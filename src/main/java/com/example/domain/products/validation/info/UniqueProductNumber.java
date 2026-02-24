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
 * productNumber に比較元フィールド名(登録・修正するパスワードのフィールド名)を指定する.
 * newreEnterPassword に比較先フィールド名(確認用パスワードのフィールド名)を指定する.
 * 
 * 例：
 * PasswordMatches(password="password", reEnterPassword="reEnterPassword")
 * 
 */
@Target({ElementType.TYPE}) 
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueProductNumberValidator.class)
@Documented
public @interface UniqueProductNumber {
	
	String message() default "商品番号が重複しているので登録できません";	// デフォルトエラーメッセージ.
    Class<?>[] groups() default {};							// groupsを指定できる仕組みの設定.
    Class<? extends Payload>[] payload() default {};
    
    String productNumber();		// productNumber属性を設定することで,各formクラスのフィールド名をバリデータクラスに渡すことができる.
}
