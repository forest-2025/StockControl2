package com.example.component;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 * SpringSecurity の User クラスを拡張して,独自のユーザー情報を持たせたクラス.
 * User クラスは基本的に username ・ password ・権限（roles / authorities）しか保持しないため,
 * このクラスで familyName と firstName と userId を追加して保持することで,
 * ログイン中のユーザーの名前をログインしているあいだ画面に表示し続けることができ,
 * パスワードの修正画面に遷移しパスワードの変更を行うことができる.
 *
 */
public class CustomUserDetails extends User{
	
	private final String familyName;
	private final String firstName;
	private final Integer userId;

	/**
	 * SpringSecurity の User クラスの情報にフルネームを保持する,
	 * CustomUserDetails のインスタンスを生成するコンストラクタ.
	 *
	 * @param username 認証に使用するユーザのメールアドレス.
	 * @param password 認証に使用するパスワード.
	 * @param authorities ユーザーが持つ権限のコレクション.
	 * @param familyName ユーザーの姓.
	 * @param firstName ユーザーの名.
	 * @param userId ユーザーのID.
	 */
	public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities,
			String familyName, String firstName,Integer userId) {
		// 親クラス(User)のコンストラクタを呼び出す.
		super(username, password, authorities);	
		
		// 独自フィールドの初期化する.
		this.familyName = familyName;
		this.firstName = firstName;
		this.userId = userId;
	}

	/**
	 * 認証済みユーザーの姓を取得する.
	 *
	 * @return ユーザーの姓.
	 */
	public String getFamilyName() {
		return familyName;
	}

	/**
	 * 認証済みユーザーの名を取得する.
	 *
	 * @return ユーザーの名.
	 */
	public String getFirstName() {
		return firstName;
	}
	
	/**
	 * 認証済みユーザーのIDを取得する.
	 *
	 * @return ユーザーのID.
	 */
	public Integer getUserId() {
		return userId;
	}

}
