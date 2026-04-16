package com.example.domain.users.validation;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * PasswordMatches アノテーションの検証処理を行う.
 * 
 * null の場合は @NotBlank に任す.
 * 一致しない場合は password フィールドにエラーを紐付ける.
 */
public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

	private String message; // エラーメッセージ;

	private String password;

	private String reEnterPassword;

	/**
	 * PasswordMatches アノテーションの初期化処理を行う.
	 * 入力されたパスワードと確認用パスワード,デフォルトのエラーメッセージをアノテーションから取得して保持する.
	 *
	 * @param annotation PasswordMatches アノテーションの属性値を持ったオブジェクト.
	 */
	@Override
	public void initialize(PasswordMatches annotation) {
		this.message = annotation.message();
		this.password = annotation.password(); // 各formクラスのpassword属性の値(フィールド名)が取得され代入されている.
		this.reEnterPassword = annotation.reEnterPassword(); // 各formクラスのreEnterPassword属性の値(フィールド名)が取得され代入されている.
	}

	/**
	 * パスワードと確認用パスワードが一致するか確認する.
	 *
	 * @param value 検証対象のオブジェクト.
	 * @param context バリデーションコンテキスト.
	 * @return フィールドが一致する場合は true,一致しない場合は false.
	 */
	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {

		if (value == null) {

			return true;
		}

		BeanWrapper beanWrapper = new BeanWrapperImpl(value);
		Object firstPassword = beanWrapper.getPropertyValue(password);
		Object secondPassword = beanWrapper.getPropertyValue(reEnterPassword);

		if (firstPassword == null || secondPassword == null) {

			return true;
		}

		if (firstPassword.equals(secondPassword)) {

			return true;

		} else {
			// エラーを特定のフィールドに紐づけて伝える
			context.disableDefaultConstraintViolation(); // デフォルトのクラスエラーを無効化.
			context.buildConstraintViolationWithTemplate(message) // デフォルトメッセージをメッセージに再設定する.
					.addPropertyNode("password")// passwordフィールドにエラーをつける.
					.addConstraintViolation(); // バリデーションエラーを確定する.

			return false;
		}

	}
}

/*コントローラーなどで引数に@Validが付いたオブジェクトが渡される						バリデーションエンジン（Hibernate Validator）が起動
 * 					↓
 *				スキャンする										オブジェクトの中を上から順に見ていきアノテーションが付いている場所（クラスやフィールド）を見つける.
 *					↓
 *＠PasswordMatchが付与されているためProxyクラスとプロキシインスタンスが作成される		特殊なインターフェース(@interface)であるPasswordMatchはアプリケーションが実行されると,
 * 				(変数 annotation)								JVMとHibernate Validator(springbootで標準実装されているアノテーションを付与することでバリデーションをしてくれるフレームワーク)
 * 															が自動的にインタフェースの実装クラスを作成し,インタフェースの設定またはアノテーションの属性(@Size(message="~")のmessageなど)をもとにオブジェクトを作成して注入してくれる.
 * 															このときに作られる実装クラスをProxyクラスといい,そのオブジェクトをプロキシ（日本語に訳すと代理人）という.このオブジェクトから属性の値を取得することができる.
 *					↓
 *@Constraintで指定されたValidatorクラスのオブジェクトを作成する				このオブジェクトでinitialize()メソッドを呼んで引数にプロキシインスタンスを渡すことで,
 *				(ここのクラスのこと)									プロキシインスタンスの値をこのValidatorクラスのフィールドに代入できる.
 *					↓										
 * ConstraintValidatorContextのオブジェクトを作成する					ConstraintValidatorContextはバリデーション失敗時の報告書をカスタマイズ(書き直し)するインタフェース.
 * 															実装クラスはConstraintValidatorContextImplクラス.
 * 					↓
 * 			isValid()メソッドを呼び出す								バリデーションエラーがないか確認する.バリデーションが失敗ならConstraintValidatorContextを書き直す.
 * 					↓
 * 			isValid()メソッドがfalse								Hibernate ValidatorがConstraintValidatorContext（報告書）を回収する.
 * 															MethodArgumentNotValidException という例外が発生し,エラー情報は BindingResultオブジェクトに自動で格納される.
 * 
 * implements ConstraintValidator<PasswordMatches, Object>　は第一引数でこのクラスがバリデーションする型(どのアノテーションか)を指定し,第二引数でチェック対象となるデータの型を指定している.
 * この第二引数は,アノテーションがフィールドについていたらそのフィールドの型を,クラスについていたらそのクラスの型を指定する.しかし今回のパスワードと確認用パスワードが一致するか確認するバリデーションでは,ユーザー登録フォームとパスワード修正フォームで使用したい.
 * このように複数で使用したい時(特定の型でないとき)は引数にObjectを指定する.
 * 
 * isValid()メソッドの第一引数のObject valueは入力されたformクラスのオブジェクトが格納されていて,そのオブジェクトをもとにBeanWrapperImplをnewすることで中身がどんな型であろうが,指定されたフィールドの値を取り出してくれる.
 * (BeanWrapperImplオブジェクトは指定されたプロパティ名から自身のgetterを探して値を渡してくれる).
 */
