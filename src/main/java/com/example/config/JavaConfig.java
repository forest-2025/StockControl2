package com.example.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class JavaConfig {

	// formクラスとエンティティクラスの変換をするため登録する.
	@Bean
	ModelMapper modelMapper() {
		return new ModelMapper();
	}

	/* ログインエラーメッセージを表示するため登録する.
	 * (アプリ起動時に警告が出るのはspringsecurityは「UserDetailsServiceがあればDaoAuthenticationProviderを自動で作る」
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
