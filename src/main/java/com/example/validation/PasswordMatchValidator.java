package com.example.validation;

import org.springframework.beans.BeanWrapperImpl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, Object> {

	private String message; // エラーメッセージ;

	private String password;

	private String reEnterPassword;

	@Override
	public void initialize(PasswordMatch annotation) {
		this.message = annotation.message();
		this.password = annotation.password();
		this.reEnterPassword = annotation.secondPassword();
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		try {
			BeanWrapperImpl rap = new BeanWrapperImpl(value);
			if (rap.getPropertyValue(password).equals(rap.getPropertyValue(reEnterPassword))) {

				return true;

			} else {
				// エラーを特定のフィールドに紐づけて伝える
				context.disableDefaultConstraintViolation(); // デフォルトのクラスエラーを無効化.
				context.buildConstraintViolationWithTemplate(message) // デフォルトメッセージを設定する.
						.addPropertyNode("password") // passwordフィールドにエラーをつける.
						.addConstraintViolation(); // バリデーションエラーを確定する.

				return false;
			}

		} catch (Exception e) {
			
			log.error("@PasswordMatch バリデーション中に例外が発生しました", e);
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
 * 															実装クラスはConstraintValidatorContextImplクラス).
 * 					↓
 * 			isValid()メソッドを呼び出す								バリデーションエラーがないか確認する.バリデーションが失敗ならConstraintValidatorContextを書き直す.
 * 					↓
 * 			isValid()メソッドがfalse								Hibernate ValidatorがConstraintValidatorContext（報告書）を回収する.
 * 															MethodArgumentNotValidException という例外が発生し,エラー情報は BindingResultオブジェクトに自動で格納される.
 * 
 * implements ConstraintValidator<PasswordMatch, Object>　は第一引数でこのクラスがバリデーションする型(どのアノテーションか)を指定し,第二引数でチェック対象となるデータの型を指定している.
 * この第二引数は,アノテーションがフィールドについていたらそのフィールドの型を,クラスについていたらそのクラスの型を指定する.しかし今回のパスワードと確認用パスワードが一致するか確認するバリデーションでは,ユーザー登録フォームとパスワード修正フォームで使用したい.
 * このように複数で使用したいバリデーションの時は台に引数にObjectを指定している.
 * 
 * isValid()メソッドの第一引数のObject valueは入力されたformクラスのオブジェクトが格納されていて,そのオブジェクトをもとにBeanWrapperImplをnewすることで中身がどんな型であろうが,指定されたフィールドの値を取り出してくれる.
 * (BeanWrapperImplオブジェクトは指定されたプロパティ名から自身のgetterを探して値を渡してくれる).
 */
