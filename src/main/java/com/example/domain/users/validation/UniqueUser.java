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
 * 指定したフィールドが既存のデータと重複していないか検証するアノテーション.
 * 
 * このアノテーションはクラスに付与する.
 * userIdField に修正するユーザーを検索できるフィールド名を指定する(idなど).
 * checkField に重複を確認するするフィールド名を指定する.
 * columnName に重複を確認するフィールドに対応するカラム名を指定する.
 * 
 * 例：
 * UniqueUser(userIdField="userId",checkField="employeeNumber",columnName="employee_number")
 * 
 */
@Target({ElementType.TYPE}) 
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueUserValidator.class)
@Repeatable(UniqueUser.List.class) 		// 同じ要素（クラス・メソッド・フィールドなど）に同一のアノテーションを複数回指定できるようになる.
@Documented
public @interface UniqueUser {

	String message() default "重複しています";	// デフォルトエラーメッセージ.
    Class<?>[] groups() default {};							// groupsを指定できる仕組みの設定.
    Class<? extends Payload>[] payload() default {};
    
    String userIdField();	// ユーザー情報修正時の重複の確認で自身の情報を除外するときの条件となる,m_userテーブルのidに対応するフィールド名を設定できる属性を設定している.
    String checkField();	// 重複を確認するフィールドを設定する属性を設定している.
    String columnName();	// 重複を確認するフィールドとそのフィールドに対応するテーブルのカラム名が異なるため,カラム名を設定する属性の設定をしている.
    
    // コンテナアノテーションを定義する.
    @Target({ ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        UniqueUser[] value();	// value()メソッドは必須.
    }

}
