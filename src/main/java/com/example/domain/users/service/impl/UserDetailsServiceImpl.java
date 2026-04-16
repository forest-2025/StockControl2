package com.example.domain.users.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.component.CustomUserDetails;
import com.example.domain.users.model.MUser;
import com.example.domain.users.service.UserService;

/**
 * ログイン時に入力されたユーザー名をもとにデータベースからユーザー情報を取得し,
 * SpringSecurity が扱える UserDetails オブジェクトを生成するサービスクラス.
 * 
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserService loginService;

	/**
	 * ログイン処理で使用されるメソッド.
	 * 入力されたユーザー名をもとにユーザーを検索し,認証に必要な情報を返す.
	 * 
	 * @param emailAddress ログイン画面で入力されたメールアドレス.
	 * @return Spring Security が認証処理に使用する UserDetails オブジェクト.
	 * @throws UsernameNotFoundException 指定されたユーザー名のユーザーが存在しない場合.
	 */
	@Override
	public UserDetails loadUserByUsername(String emailAddress) throws UsernameNotFoundException {

		// ログインユーザー情報を取得する(削除済みは除く).
		MUser loginUser = loginService.getByEmailAddress(emailAddress);

		// ユーザーが存在しない場合.
		if (loginUser == null) {
			throw new UsernameNotFoundException("ユーザーが見つかりません");
		}

		/* ユーザーがログインしている間はユーザーの姓と名に"さん"をつけた名前をヘッダーに表示しておく.
		 * それには情報が消えないようにセッションに登録する必要があるため,UserクラスをカスタマイズしたCustomUserDetailsクラスを使い登録する.
		 * また,ヘッダーにパスワード修正画面へのリンクをつけるためユーザーIDも登録している. */
		CustomUserDetails customUserDetails = new CustomUserDetails(loginUser);

		// UserDetailsを生成する.
		UserDetails userDetails = (UserDetails) customUserDetails;

		return userDetails;
	}

}