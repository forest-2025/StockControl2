package com.example.config.security;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
public class SecurityBeanConfig {
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	/**
	 * ユーザー名・パスワード認証を行う AuthenticationProvider を
	 * Spring の Bean として登録する.
	 *
	 * @param passwordEncoder パスワード照合に使用するエンコーダ.
	 * @param userDetailsService ユーザー情報取得用サービス.
	 * @param messageSource 認証エラーメッセージ用.
	 * @return {@link AuthenticationProvider}
	 */
	/* ログインエラーメッセージを表示するため登録する.
	 * (アプリ起動時に警告が出るのはSpringSecurityは「UserDetailsServiceがあればDaoAuthenticationProviderを自動で作る」
	 * という仕組みを持っているがここで独自で定義しているので自動で作らないといっている). */
	@Bean
	AuthenticationProvider daoAuthenticationProvider(PasswordEncoder passwordEncoder,
			UserDetailsService userDetailsService, MessageSource messageSource) {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder);
		provider.setMessageSource(messageSource);

		return provider;
	}

}

/* リクエスト	POSTリクエスト　username(ユーザーID・メールアドレスなど) password(パスワード).
  ↓
SecurityFilterChain		コントローラクラスにリクエストが渡される前に,そのリクエストをコントローラに渡してよいかを判断するための順序や処理方法を定義したもの.
  ↓
AuthenticationFilter	リクエストがログイン処理かどうかを判断して,ログイン処理ならフォームに入力された username　・　password を取得する.
						Authentication インタフェースの実装クラス
						(UsernamePasswordAuthenticationToken ユーザーが入力したユーザー名とパスワードをもとに生成される,
						認証処理用のデータオブジェクト(トークン))のオブジェクトに username　・　password を引数に渡してオブジェクトを生成し,
						Authentication 型の変数に 代入する(このとき実装クラスのフィールドauthenticatedはfalse（未認証）で作成される).
						作成されたオブジェクトを AuthenticationManager に渡す.
  ↓
AuthenticationManager	複数の AuthenticationProvider を管理している(実際は実装したProviderManagerが管理してる).
						リクエストに来た Authentication オブジェクトを受け取り,どの Provider が処理できるか順番に確認し,
						処理できる Provider が見つかれば 認証を任せる.
						認証成功なら Authentication（authenticated=true）を返し,全ての Provider で失敗したら例外を投げる.
  ↓
AuthenticationProvider	ここでBean登録されているのはここの部分のこと.これはアプリケーション起動時に Bean 登録される内容のこと.
						これは誰を認証対象にするか,パスワードの照合ルール,エラーメッセージ取得方法など,認証方法を保持するオブジェクトを作成している.
						@Configuration クラスで内でBeanを定義するメソッドの引数に,他の Bean を自動的に渡して（注入して）利用する仕組みがある.
						(これを @Bean メソッドの引数による依存性注入という).
						そのため,
						AuthenticationProvider daoAuthenticationProvider(PasswordEncoder passwordEncoder,
						UserDetailsService userDetailsService, MessageSource messageSource)
						は, PasswordEncoder の実装クラス(このクラスで Bean 登録されている BCryptPasswordEncoder)のオブジェクト,
						UserDetailsService の実装クラス(UserDetailsServiceImpl のこと @Service がついて Bean 登録されている)のオブジェクト,
						MessageSource の実装クラス(MessageSource は Spring のデフォルトで自動的に Bean 登録されるため,
						こちらで実装クラスを登録しなくても使用できる(国際化対応(ユーザーの言語環境に合わせて適切なメッセージを自動的に切り替えるため),
						メッセージの外部化(ソースコード（Java）内にメッセージをハードコードするのではなく,設定ファイルに分離するため),
						パラメータの埋め込み({0},{1}のようなプレースホルダーをメッセージ内に含め,実行時に動的なパラメータを埋め込むため).
						などをするのに必要なため自動的に登録される.)
						実装クラスは ResourceBundleMessageSource がデフォルトと思われる)のオブジェクトが注入されている.
						(これにより messages.properties で設定されている,
						AbstractUserDetailsAuthenticationProvider.badCredentials=メールアドレスまたはパスワードが違います が使用できるようになる).
						これをコンストラクタとセッター？(ドキュメントにフィールドがなかったから違うと思う.)に渡すことで認証方法を保持するオブジェクトを作成され,認証する準備が整う.
						リクエストがきて AuthenticationManager から認証を任されると,authenticate()メソッドが呼ばれてる.
						それにより, AuthenticationFilter でつくられた　Authentication 型のオブジェクトから username　・　password を取得する.
						UserDetailsService.loadUserByUsername(username) を呼び出して DB や他のユーザー情報ソースからユーザー情報を取得する.
						取得した UserDetails(loadUserByUsername(username)の戻り値)から DBに保存されているパスワードを取り出す.
						PasswordEncoder.matches(formPassword, dbPassword)でフォーム入力のパスワードとDBパスワードを照合する.
  ↓
認証成功 or 失敗			照合成功 → authenticated=true の Authentication オブジェクトを返す.
						この時の Authentication オブジェクトはAuthenticationFilterとは別で,新しく作られたオブジェクト.
						authenticated=true に設定され UserDetails(DB から取得した情報)等を保持し,ログイン時に入力された username　・　password は,
						保持していない.(UserController クラスの postEdit()メソッドなどの Authentication authentication は多分これ).
						SecurityFilterChain の .defaultSuccessUrl()メソッドの引数の遷移先URLパスにリダイレクトする.
						
						照合失敗 → 例外を投げ, MessageSource でエラーメッセージを取得する.
						(throw new BadCredentialsException(
    						messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        				)のように　messages.properties のメッセージがキー名により取得され例外メッセージとして設定され, AuthenticationFailureHandler が,
        				HTTPセッションに例外オブジェクト(例外メッセージも含めて)を SPRING_SECURITY_LAST_EXCEPTION というキー名で保存する.
        				これによりビューに例外メッセージを渡すことができる.
        				
						SpringSecurity の ProviderManager は,
						複数の AuthenticationProvider を持てるようになっていて,この Bean がその一つとして登録される.
						このアプリケーションではこれしか登録してないため,これしか使えない.
 */
