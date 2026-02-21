package com.example.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Repeatable(PasswordMatch.List.class)
@Target({ElementType.TYPE}) 
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatchValidator.class)
public @interface PasswordMatch {
	
	    String message() default "パスワードと確認用パスワードが一致しませんでした";
	    Class<?>[] groups() default {};
	    Class<? extends Payload>[] payload() default {};
	    
	    String password(); // パスワード
	    String secondPassword(); // 確認用パスワード
	    
	    @Target({ ElementType.TYPE })
	    @Retention(RetentionPolicy.RUNTIME)
	    @interface List {
	    	PasswordMatch[] value();
	    }

}

/* @interfaceはアノテーションを設定するための特別なインターフェースであることを示すキーワード(予約語).
 * アノテーションの設定をするメソッドしか持つことができない.
 * @Constraint(validatedBy = PasswordMatchValidator.classはHibernate Validator
 * に対する「このアノテーションの検査には、このクラスをnewして使ってください」という指示書のようなもの.
 * Class<?>[] groups() default {};はgroupsを指定できる仕組みを用意してる(配列なのは複数のグループを同時に設定できるようにするため).
 * Class<? extends Payload>[] payload() default {};は検証エラー時の付加情報（メタデータ）を定義する.
 * バリデーションエラーに付随する情報（例えば、警告、エラー、深刻なエラーなど）を定義するために使う.*/
