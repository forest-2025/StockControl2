package com.example.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.TYPE}) 
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatchValidator.class)
public @interface PasswordMatch {
	
	    String message() default "パスワードと確認用パスワードが一致しませんでした";
	    Class<?>[] groups() default {};
	    Class<? extends Payload>[] payload() default {};

}

/* @interfaceはアノテーションを設定するための特別なインターフェースであることを示すキーワード(予約語).
 * アノテーションの設定をするメソッドしか持つことができない.
 * Class<?>[] groups() default {};はgroupsを指定できる仕組みを用意してる(配列なのは複数のグループを同時に設定できるようにするため).
	 検証エラー時の付加情報（メタデータ）を定義する.バリデーションエラーに付随する情報（例えば、警告、エラー、深刻なエラーなど）を定義するために使う.*/
