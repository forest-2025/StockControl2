package com.example.config.security;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SpringSecurityのフィルタチェーンを設定クラス.
 * 
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		// リクエストの制御(直リンクの禁止).
		http.authorizeHttpRequests(authorize -> authorize
				.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()// 静的リソースのファイルパスにログインなしでアクセスOK.
				.anyRequest().authenticated()) // それ以外は直リンク禁止する.

				// ログインに関する設定.
				.formLogin(login -> login
						.loginPage("/login") 				// 自作ログインページのためURLパスを指定する(GETリクエスト).
						.loginProcessingUrl("/login") 		// ログインページでログインボタンを押した時の遷移先のURLパス.(POSTリクエストでこちらに遷移したら認証処理が行われる).
						.failureUrl("/login?error") 		// ログイン失敗時の遷移先URLパスを指定する.
						.usernameParameter("emailAddress") 	// ログインページのメールアドレス.
						.passwordParameter("password") 		// ログインページのパスワード.
						.defaultSuccessUrl("/products/info/list", true) // ログイン成功後の遷移先URLパスを指定.
						.permitAll()						// 未ログインのユーザーでもログインページにアクセスできるようにする.﻿

				// ログアウト処理.
				).logout(logout -> logout
						.logoutUrl("/logout") 			// ログアウトのURLパスを指定する(POSTリクエストで送られてくるものを受け付ける).
						.logoutSuccessUrl("/logout") 	// ログアウト成功時の遷移先URLパス(リダイレクトするのでGETリクエストでURLパスに遷移する).
						.invalidateHttpSession(true)	// サーバー側のHTTPセッションを破棄(デフォルトで有効だが明示的に記載している).
						.clearAuthentication(true)		// 現在のThreadLocalのSecurityContextに入っているAuthenticationをクリア(デフォルトで有効だが明示的に記載している).
					    .deleteCookies("JSESSIONID")	// ブラウザ側のクッキーを削除する.
						.permitAll()					// 独自のログアウト成功エンドポイントを指定したので、未ログインユーザーでもアクセスできるようにする.

				);

		return http.build();
	}

}

/* springsecurityではキャッシュについてはデフォルトでブラウザのキャッシュが無効になるようにHTTPレスポンスヘッダが付与されるため,
 * 基本的には何も設定しなくていい.
 * Cache-Control: no-cache, no-store, max-age=0, must-revalidate と,
 * Pragma: no-cache と,
 * Expires: 0 が自動的に設定される.
 * Cache-Controlヘッダーはブラウザなどがコンテンツをキャッシュする方法や、期間、再利用のルールなどを制御するためのHTTPヘッダ-のこと.
 * no-cache, no-store, max-age=0, must-revalidateはCache-Controlヘッダーのディレクティブ(指示語).
 * PragmaヘッダーはHTTP/1.0時代のヘッダーであり、古いシステムとの下位互換性のために使用される. 
 * Expiresヘッダーはブラウザやプロキシが、そのレスポンスをいつまでキャッシュしてよいかを示すための日時を指定するヘッダーで,
 * 0は有効期限が0(有効期限なし)なのでキャッシュが無効のためサーバーに問い合わせて最新の情報を取得するということ.
 * (保存するが期限切れで使用しないか,そもそも保存しないかはブラウザによる).
 * これらはBFCacheには完全には効かないが、通常のキャッシュは無効化できる.
 * <meta>タグも似ているが<meta>タグは適用タイミングが遅く,無視されやすい.
 * 今回はspringsecurityを使用しているので<meta>タグは記載してない. */
