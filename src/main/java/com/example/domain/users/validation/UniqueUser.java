package com.example.domain.users.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * 入力された商品番号が m_product に登録されている商品番号と重複していないか検証するアノテーション.
 * 
 * このアノテーションはクラスに付与する.
 * productIdField に登録・修正する商品のIDのフィールド名を指定する.
 * productNumberField に登録・修正する商品の商品番号のフィールド名を指定する.
 * 
 * 例：
 * UniqueProductNumber(productIdField="productId",productNumberField="productNumber")
 * 
 */
@Target({ElementType.TYPE}) 
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueUserValidator.class)
@Repeatable(UniqueUser.List.class) 
@Documented
public @interface UniqueUser {

	String message() default "";	// デフォルトエラーメッセージ.
    Class<?>[] groups() default {};							// groupsを指定できる仕組みの設定.
    Class<? extends Payload>[] payload() default {};
    
    String userIdField();
    String checkField();
    String columnName();
    
    @Target({ ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        UniqueUser[] value();
    }

}
