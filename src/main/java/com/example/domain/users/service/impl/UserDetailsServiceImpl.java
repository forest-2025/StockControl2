package com.example.domain.users.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.component.FullNameUser;
import com.example.domain.users.service.UserService;
import com.example.dto.common.MUser;

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

		// ユーザーの管理者権限を取得する(0または1).
		Integer userAuthorityNo = loginUser.getIsAdmin();

		// 権限リストを作成する(リストなのはユーザーが複数権限を持つ可能性があるから).
		List<GrantedAuthority> authorities = new ArrayList<>();

		/* isAdmin が0なら管理者権限なし(GENERAL).
		 * 1なら管理者権限あり(ROLE_ADMIN)を設定. */
		if (userAuthorityNo == 0) {

			// 権限の文字列を保持したSimpleGrantedAuthorityオブジェクトをキャストすることで﻿権限情報を設定する.
			GrantedAuthority authority = new SimpleGrantedAuthority("GENERAL");
			// 権限リストに追加する.
			authorities.add(authority);
		} else  {
			GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_ADMIN");
			// 権限リストに追加する.
			authorities.add(authority);
		}

		/* ユーザーがログインしている間はユーザーの姓と名に"さん"をつけた名前をヘッダーに表示しておく.
		 * それには情報が消えないようにセッションに登録する必要があるため、UserクラスをカスタマイズしたFullNameUserクラスを使い登録する. */
		FullNameUser fullNameUser = new FullNameUser(loginUser.getEmailAddress(), loginUser.getPassword(),
				authorities, loginUser.getFamilyName(), loginUser.getFirstName());

		// UserDetailsを生成する.
		UserDetails userDetails = (UserDetails) fullNameUser;

		return userDetails;
	}

}
/* SecurityBeanConfig クラスでも記載しているが,認証に成功したときに作成されるのは authenticated=true に設定され,
 * UserDetails(このクラスの loadUserByUsername()メソッドで作成された DB から取得した情報をもつ UserDetails オブジェクト)
 * 等を保持する Authentication オブジェクト.
 * @AuthenticationPrincipal UserDetails userDetails
 * @AuthenticationPrincipal FullNameUser fullNameUser
 * で渡されるオブジェクトは同じオブジェクトだが,受け取る型が違う(インタフェースか実装クラスか).
 * 
 * UserDetails は実際は FullNameUser オブジェクトが入っているが,型が UserDetails のため UserDetails のメソッドしか扱えず,
 * FullNameUser のフィールドにアクセスするには instanceof を使用して型チェックをしないといけない.
 * (別のが入っていたら ClassCastException になる).
 * 
 * FullNameUser はそのまま自身のフィールドやメソッドを使用できる.
 * しかし, Authentication オブジェクトのなかの principal が FullNameUser でなければ ClassCastException になる.
 * 
 * Authentication の principal はフィールドではなくプロパティ(インタフェースなのでフィールドはないが getPrincipal()メソッドがある).
 * これは principal という属性が“取得できること”を実装クラスに強制している.
 * 実装クラスの principal という属性とは getter によって外部から取得できる論理的な値のことで,その実体はフィールドでも,
 * 計算結果でも,別のオブジェクトでもいい.
 * */
