package com.example.component;

import java.util.Collection;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.domain.users.model.MUser;
 
/**
 * UserDetails インタフェースを実装した独自のユーザー情報を持たせたクラス.
 * 基本的な username ・ password と権限に関する role,
 * 独自フィールドとして familyName ・ firstName ・ userId を追加して保持している.
 * これによりログイン中のユーザーの名前をログインしているあいだ画面に表示し続けることができ,
 * またパスワードの修正ができる.
 */
public class CustomUserDetails implements UserDetails {

	private final String userName;
	private String password;
	private final Set<SimpleGrantedAuthority> role;
	private final String familyName;
	private final String firstName;
	private final Integer userId;

	/**
	 * CustomUserDetails のインスタンスを生成するコンストラクタ.
	 *
	 * @param user 認証に使用するユーザー.
	 */
	public CustomUserDetails(MUser user) {
		
		this.userName = user.getEmailAddress();
		this.password = user.getPassword(); 
		this.role = Set.of(new SimpleGrantedAuthority(user.getRole())); 
		this.familyName = user.getFamilyName();
		this.firstName = user.getFirstName();
		this.userId = user.getUserId();
	}
	
	/**
	 * ユーザーの姓を取得する.
	 *
	 * @return ユーザーの姓.
	 */
	public String getFamilyName() {
		
		return this.familyName;
	}

	/**
	 * ユーザーの名を取得する.
	 *
	 * @return ユーザーの名.
	 */
	public String getFirstName() {
		
		return this.firstName;
	}

	/**
	 * ユーザーのIDを取得する.
	 *
	 * @return ユーザーのID.
	 */
	public Integer getUserId() {
		
		return this.userId;
	}

	/**
	 * ユーザーの姓と名を取得する.
	 *
	 * @return ユーザーの姓と名.
	 */
	public String getFullName() {
		
		return this.familyName + " " + this.firstName;
	}
	
	/**
	 * ユーザーに付与された権限を取得する.
	 * ページ閲覧などのアクセス制御（認可）に使用する.
	 *
	 * @return ユーザーに付与された権限のコレクション.
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		return this.role;
	}

	/**
	 * ユーザーの認証に使用するパスワードを返す.
	 * eraseCredentials() メソッド実行後は null を返す.
	 *
	 * @return ハッシュ化されたパスワード文字列,認証成功後は null.
	 */
	@Override
	public String getPassword() {
		
		return this.password;
	}

	/**
	 * ユーザーのユーザー名(メールアドレス)を取得する.
	 *
	 * @return ユーザー名(メールアドレス).
	 */
	@Override
	public String getUsername() {
		
		return this.userName;
	}

}