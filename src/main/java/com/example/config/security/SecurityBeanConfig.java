package com.example.config.security;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * SpringSecurity の認証に関する設定を行うクラス.
 *
 * ユーザー名とパスワードによる認証に必要な AuthenticationProvider や
 * PasswordEncoder を Bean として定義している.
 *
 * パスワードは安全な形に変換して扱われ,ユーザー情報の取得や認証処理は SpringSecurity
 * によって行われる.
 * 
 */
@Configuration
public class SecurityBeanConfig {

	/**
	 * パスワードを暗号化して扱うための PasswordEncoder オブジェクトを Bean として定義する.
	 *
	 * BCrypt を使ってパスワードを安全な形に変換し,保存や照合に利用する.
	 *
	 * @return パスワードの暗号化と検証に使用する PasswordEncoder のオブジェクト.
	 */
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * ユーザー名・パスワード認証を行う DaoAuthenticationProvider のオブジェクトを
	 * Bean として定義する.
	 * 
	 * UserDetailsService からユーザー情報を取得し,
	 * PasswordEncoder を使ってパスワードを検証する.
	 *
	 * @param passwordEncoder パスワード照合に使用するエンコーダ.
	 * @param userDetailsService ユーザー情報取得用サービス.
	 * @param messageSource 認証エラーメッセージ用.
	 * @return  認証に使用する AuthenticationProvider オブジェクト.
	 */
	// ログインエラーメッセージを表示するため登録する.
	@Bean
	AuthenticationProvider daoAuthenticationProvider(PasswordEncoder passwordEncoder,
			UserDetailsService userDetailsService, MessageSource messageSource) {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder);
		provider.setMessageSource(messageSource);

		return provider;
	}

}
