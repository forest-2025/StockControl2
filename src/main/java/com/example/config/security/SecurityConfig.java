package com.example.config.security;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SpringSecurity のフィルタチェーンを設定するクラス.
 * 
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	/**
	 * アプリケーション全体のセキュリティ設定を行う SecurityFilterChain を Bean として定義する.
	 *
	 * どのURLに認証が必要か,ログインやログアウトの方法などWebセキュリティの基本的な動作をここで設定する.
	 *
	 * @param http HttpSecurity オブジェクト.セキュリティ設定の操作対象.
	 * @return セキュリティ設定が適用された SecurityFilterChain.
	 * @throws Exception 設定時にエラーが発生した場合.
	 * 
	 */
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		// リクエストの制御(直リンクの禁止).
		http.authorizeHttpRequests(authorize -> authorize
				.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()// 静的リソースのファイルパスにログインなしでアクセスOK.
				.requestMatchers(PathRequest.toH2Console()).permitAll() // H2DBコンソールを使用できるよう設定している.
				.requestMatchers("/login").permitAll()
				.requestMatchers("/logout").permitAll()
				.requestMatchers("/upload/**").authenticated() // 外部フォルダを設定する.
				.requestMatchers("/products/info/list").authenticated()
				.requestMatchers("/products/*/count/arrive").authenticated()
				.requestMatchers("/products/*/count/ship").authenticated()
				.requestMatchers("/products/*/info/display-details").authenticated()
				.anyRequest().hasRole("ADMIN")// それ以外は直リンク禁止する.

		/* 1.ブラウザからのリクエスト
		 * 			↓
		 * 2.Spring Security Filter ← ここで「権限なし！」と判定・遮断されてエラーへ
		 * 			↓
		 * 3.DispatcherServlet（Spring MVCの入り口）
		 * 			↓
		 * 4.@ControllerAdvice / @ExceptionHandler ← ここまでリクエストが届かない
		 * 			↓
		 * 5.Controller
		 * 
		 * @ControllerAdvice は、リクエストが 3番（DispatcherServlet）以降に進まないと発動しない. */
		 ).exceptionHandling(exception -> exception	// 管理者権限のない人が管理者のページに行こうとしたときに独自のエラー画面に遷移するときに設定する(403エラー)
				.accessDeniedPage("/error/403") // ← ここでパスを指定してそのパスのコントローラを作ってそこにとばして独自のエラー画面に遷移できる.これを設定しないとブラウザのエラー画面がでる

		// ログインに関する設定.
		).formLogin(login -> login
				.loginPage("/login") // 自作ログインページのためURLパスを指定する(GETリクエスト).
				.loginProcessingUrl("/login") // ログインページでログインボタンを押した時の遷移先の URLパス.(POSTリクエストでこちらに遷移したら認証処理が行われる).
				.failureUrl("/login?error") // ログイン失敗時の遷移先URLパスを指定する(リダイレクトなのでGETリクエスト).
				.usernameParameter("emailAddress") // ログインページのメールアドレス.
				.passwordParameter("password") // ログインページのパスワード.
				.defaultSuccessUrl("/products/info/list", true) // ログイン成功後の遷移先URLパスを指定する(リダイレクトなのでGETリクエスト).
				.permitAll() // 未ログインのユーザーでもログインページにアクセスできるようにする.﻿

		// ログアウト処理.
		).logout(logout -> logout
				.logoutUrl("/logout") // ログアウトのURLパスを指定する(POSTリクエストで送られてくるものを受け付ける).
				.logoutSuccessUrl("/logout") // ログアウト成功時の遷移先URLパス(リダイレクトするのでGETリクエストでURLパスに遷移する).
				.invalidateHttpSession(true) // サーバー側のHTTPセッションを破棄(デフォルトで有効だが明示的に記載している).
				.clearAuthentication(true) // 現在の ThreadLocal の SecurityContext に入っている Authentication をクリア(デフォルトで有効だが明示的に記載している).
				.deleteCookies("JSESSIONID") // ブラウザ側のクッキーを削除する.
				.permitAll() // 独自のログアウト成功エンドポイントを指定したので、未ログインユーザーでもアクセスできるようにする.

		// X-Flame-Optionsを無効にする設定（H2DBコンソールを使用できるよう設定している）.
		).headers(headers -> headers
				.frameOptions(FrameOptionsConfig::disable)

		// CSRF 対策を無効に設定(H2DBコンソールを使用できるよう設定している).
		).csrf(csrf -> csrf
				.ignoringRequestMatchers(PathRequest.toH2Console()));

		return http.build();
	}

}

/* SpringSecurity ではキャッシュについてはデフォルトでブラウザのキャッシュが無効になるようにHTTPレスポンスヘッダが付与されるため,
 * 基本的には何も設定しなくていい.
 * Cache-Control: no-cache, no-store, max-age=0, must-revalidate と,
 * Pragma: no-cache と,
 * Expires: 0 が自動的に設定される.
 * Cache-Control ヘッダーはブラウザなどがコンテンツをキャッシュする方法や、期間、再利用のルールなどを制御するためのHTTPヘッダ-のこと.
 * no-cache, no-store, max-age=0, must-revalidate は Cache-Control ヘッダーのディレクティブ(指示語).
 * PragmaヘッダーはHTTP/1.0時代のヘッダーであり、古いシステムとの下位互換性のために使用される. 
 * Expires ヘッダーはブラウザやプロキシが、そのレスポンスをいつまでキャッシュしてよいかを示すための日時を指定するヘッダーで,
 * 0は有効期限が0(有効期限なし)なのでキャッシュが無効のためサーバーに問い合わせて最新の情報を取得するということ.
 * (保存するが期限切れで使用しないか,そもそも保存しないかはブラウザによる).
 * これらは BFCache には完全には効かないが、通常のキャッシュは無効化できる.
 * <meta>タグも似ているが <meta> タグは適用タイミングが遅く,無視されやすい.
 * 今回は SpringSecurity を使用しているので <meta> タグは記載してない. 
 * 
 * アプリが起動される.
 * 		↓
 * @Bean で SecurityFilterChain が作られる.
 * 		↓
 * Spring が ApplicationContext に登録する.
 * 		↓
 * SpringSecurity が起動時に ApplicationContext から すべての SecurityFilterChain Bean を探す.
 * 		↓
 * FilterChainProxy (全リクエストを横取りするフィルタ)の内部に取り込む. 
 * 		↓
 * リクエストが来れば FilterChainProxy が適切な SecurityFilterChain を選択してチェーン内のフィルター順に実行する.
 * 		↓
 * 問題なければ Controller に到達する. */
