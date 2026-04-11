package com.example.config.security;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
@EnableMethodSecurity
public class SecurityConfig {

	/**
	 * H2Datebaseのセキュリティ設定を行う SecurityFilterChain を Bean として定義する.
	 *
	 * securityFilterChain メソッドより優先される.
	 *
	 * @param http HttpSecurity オブジェクト.セキュリティ設定の操作対象.
	 * @return セキュリティ設定が適用された SecurityFilterChain.
	 * @throws Exception 設定時にエラーが発生した場合.
	 * 
	 */
	@Bean
	@Order(1)
	SecurityFilterChain h2ConsoleSecurityFilterChain(HttpSecurity http) throws Exception {
		http
				.securityMatcher(PathRequest.toH2Console()) // securityMatcherはこのフィルターが特定のURLだけを対象にするときに使用するメソッドであることを意味する.spring.h2.console.path
				.authorizeHttpRequests(authorize -> authorize
						.anyRequest().permitAll())
				.headers(headers -> headers
						.frameOptions(FrameOptionsConfig::sameOrigin))
				.csrf(csrf -> csrf
						.ignoringRequestMatchers(PathRequest.toH2Console()));
		return http.build();
	}

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
	@Order(2)
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		// リクエストの制御(直リンクの禁止).
		http.authorizeHttpRequests(authorize -> authorize
				.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()// 静的リソースのファイルパスにログインなしでアクセスOK.
				.requestMatchers(PathRequest.toH2Console()).permitAll() // H2DBコンソールを使用できるよう設定している.
				.requestMatchers("/login").permitAll()
				.requestMatchers("/logout").permitAll()
				.requestMatchers("/error/**").permitAll()
				.requestMatchers("/upload/**").authenticated() // 外部フォルダを設定する.
				.requestMatchers("/products/info/list").authenticated()
				.requestMatchers("/products/{productId}/count/arrive").authenticated()
				.requestMatchers("/products/{productId}/count/ship").authenticated()
				.requestMatchers("/products/{productId}/info/display-details").authenticated()
				.requestMatchers("/users/{userId}/passwordEdit").authenticated()
				.requestMatchers("/products/info/register").hasRole("ADMIN")
				.requestMatchers("/products/{productId}/info/edit").hasRole("ADMIN")
				.requestMatchers("/products/{productId}/info/imageEdit").hasRole("ADMIN")
				.requestMatchers("/products/{productId}/info/delete").hasRole("ADMIN")
				.requestMatchers("/products/{productId}/count/edit").hasRole("ADMIN")
				.requestMatchers("/users/list").hasRole("ADMIN")
				.requestMatchers("/users/register").hasRole("ADMIN")
				.requestMatchers("/users/{userId}/edit").hasRole("ADMIN")
				.requestMatchers("/users/{userId}/delete").hasRole("ADMIN")
				.requestMatchers("/customers/**").hasRole("ADMIN")
				.requestMatchers("/suppliers/**").hasRole("ADMIN")
				.anyRequest().permitAll()

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
				.permitAll()); // 独自のログアウト成功エンドポイントを指定したので、未ログインユーザーでもアクセスできるようにする.

		return http.build();
	}

}

