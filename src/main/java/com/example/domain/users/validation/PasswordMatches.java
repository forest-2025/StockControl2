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
	
	    String message() default "{PasswordMatches.message}";	// デフォルトエラーメッセージ.
	    Class<?>[] groups() default {};									// groupsを指定できる仕組みの設定.
	    Class<? extends Payload>[] payload() default {};
	    
	    String password(); 
	    String reEnterPassword(); 
	    
}

/* @interfaceはアノテーションを設定するための特別なインターフェースであることを示すキーワード(予約語).
 * アノテーションの設定をするメソッドしか持つことができない.
 * @Constraint(validatedBy = PasswordMatchesValidator.classはHibernate Validator
 * に対する「このアノテーションの検査には、このクラスをnewして使ってください」という指示書のようなもの.
 * Class<?>[] groups() default {};はgroupsを指定できる仕組みを用意してる(配列なのは複数のグループを同時に設定できるようにするため).
 * Class<? extends Payload>[] payload() default {};は検証エラー時の付加情報（メタデータ）を定義する.
 * バリデーションエラーに付随する情報（例えば、警告、エラー、深刻なエラーなど）を定義するために使う.
 * 
 * String password(); と String reEnterPassword(); を設定することで,
 * PasswordMatches(password="password", reEnterPassword="reEnterPassword")属性が設定できる.
 * これを設定することで例えば登録フォームのフィールド名がそれぞれpassword・reEnterPasswordで,
 * 修正フォームのフィールド名がnewPassword・newReEnterPasswordでも,
 * 登録フォーム @PasswordMatches(password="password", reEnterPassword="reEnterPassword")
 * 修正フォーム @PasswordMatches(password="newPassword", reEnterPassword="newReEnterPassword")
 * でフィールド名をバリデータ―に渡すことができるようになる.
 * (属性という変数に調べたいフィールド名を代入(設定)することで,バリデータクラスで動的に調べたいフィールド名にアクセスできるようになり,フォームで入力されたそのフィールドの値を取得できる).
 * */
