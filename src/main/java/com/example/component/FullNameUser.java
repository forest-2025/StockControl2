package com.example.component;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 * SpringSecurity の User クラスを拡張して,独自のユーザー情報を持たせたクラス.
 * User クラスは基本的に username ・ password ・権限（roles / authorities）しか保持しないため,
 * このクラスで familyName と firstName を追加して保持することで,
 * ログイン中のユーザーの名前をログインしているあいだ画面に表示し続けることができる.
 *
 */
public class FullNameUser extends User{
	
	private final String familyName;
	private final String firstName;

	/**
	 * SpringSecurity の User クラスの情報にフルネームを保持する,
	 * FullNameUser のインスタンスを生成する.
	 *
	 * @param username 認証に使用するユーザのメールアドレス.
	 * @param password 認証に使用するパスワード.
	 * @param authorities ユーザーが持つ権限のコレクション.
	 * @param familyName ユーザーの姓.
	 * @param firstName ユーザーの名.
	 */
	public FullNameUser(String username, String password, Collection<? extends GrantedAuthority> authorities,
			String familyName, String firstName) {
		// 親クラス(User)のコンストラクタを呼び出す.
		super(username, password, authorities);	
		
		// 独自フィールドの初期化する.
		this.familyName = familyName;
		this.firstName = firstName;
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
	

}
