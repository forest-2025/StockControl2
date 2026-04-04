package com.example.component;

import java.util.Collection;
import java.util.Set;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.domain.users.model.MUser;
 
/**
 * UserDetails インタフェースを実装した独自のユーザー情報を持たせたクラス.
 * 基本的な username ・ password と権限に関する role,
 * 独自フィールドとして familyName ・ firstName ・ userId を追加して保持している.
 * これによりログイン中のユーザーの名前をログインしているあいだ画面に表示し続けることができ,
 * パスワードの修正画面に遷移しパスワードの変更を行うことができる.
 *
 */
public class CustomUserDetails implements UserDetails, CredentialsContainer  {

	private final String userName;
	private String password;			// eraseCredentials()でnullに変更するため,finalがついてない.
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

	/**
	 * 認証成功後,SecurityContext に保存される前に機密情報を消去する.
	 * フレームワークが自動的に呼び出して消去してくれる.
	 * 
	 */
	@Override
	public void eraseCredentials() {
		this.password = null;

	}
}

/* UserDetails は Spring Security においてユーザー情報（ユーザー名、パスワード、権限など）を保持・提供するための核となるインターフェース.
 * データベースなどから取得したユーザー情報を認証・認可処理で利用可能な形にカプセル化する役割を持ち,ログイン処理や権限チェックの基盤となる.
 * UserDetails はインタフェースのため実装したクラスが必要でデフォルトの実装クラスがUserクラス.
 * このUserクラスはこれらのフィールドをもっている(erialVersionUIDは Javaシリアライズ用の識別子で安全なシリアライズのため
 * loggerは内部ログ出力用,デバッグや警告のためなので認証・認可には関係ない).
 * 
 * username					String		ユーザー名
 * password					String		パスワード（認証時に使用）
 * authorities				Collection<? extends GrantedAuthority>	権限リスト（ROLE_USER など）
 * accountNonExpired		boolean		アカウント有効期限が切れていないか
 * accountNonLocked			boolean		アカウントがロックされていないか
 * credentialsNonExpired	boolean		パスワードの有効期限が切れていないか
 * enabled					boolean		アカウントが有効かどうか
 * 
 * このようにUserクラスはユーザー名・パスワード・権限・アカウント状態を保持するシンプルな実装になっている.
 * そのため独自の設定を追加したい場合はUserクラスを拡張する.
 * しかし複雑な設定をしたい場合はUserDetailsインタフェースを実装したクラスを作成する.
 * その際,
 * 	public class CustomUserDetails implements UserDetails,CredentialsContainer{
 * 		
 * 		～フィールドを設定する～
 * 
 *   	@Override
    	public void eraseCredentials() {
        	this.password = null; 
 * }
 * とする.
 * UserDetails だけでなく CredentialsContainer も実装することで,
 * 認証後に作成される Authentication (デフォルトは実装されたUsernamePasswordAuthenticationTokenクラス)のオブジェクトの
 * フィールド
 * 	principal   → UserDetails（CustomUserDetailsなど）	UsernamePasswordAuthenticationTokenのフィールド.
 * 	credentials → フォームに入力された生のパスワード ("12345")		UsernamePasswordAuthenticationTokenのフィールド.
 * 	authorities → UserDetails.getAuthorities()		UsernamePasswordAuthenticationTokenが拡張しているクラスのフィールド.
 * 	isAuthenticated() → true						UsernamePasswordAuthenticationTokenが拡張しているクラスのフィールド.
 * の principal のなかの UserDetails(の実装クラス)のパスワード(DBから取得したハッシュ化されたパスワード)を自動的に消去することができる.
 * 認証後に作成される Authenticationオブジェクトは HttpSession に SPRING_SECURITY_CONTEXT というキー名で,
 * SecurityContext のなかに保持される.HttpSession のなかにハッシュ化したパスワードを残しておくのはセキュリティ的によくないため,
 * CredentialsContainer インタフェースを実装して,eraseCredentials()をオーバーライドすることで自動的に削除されるようにする.
 * 今回,eraseCredentials()は単純に this.password = null にしているが,
 * 例えば生のパスワードやワンタイムパスワード,秘密の質問の答えなどをフィールドに持つときはこれらも null に変更することもできる.
 * 認証後の Authentication オブジェクトのフィールド credentials に生のパスワードがあるが,
 * こちらは UsernamePasswordAuthenticationToken クラスが拡張している AbstractAuthenticationToken クラスが,
 * CredentialsContainer を実装しているため, eraseCredentials()メソッドが自動的に削除してくれるため問題ない.
 * 
 * コントローラクラスで使用するとき
 * 
 * 	@AuthenticationPrincipal UserDetails userDetails	
 * 		型: UserDetails	
 *  	取得できる情報: username, authorities などの UserDetails インタフェースの標準メソッドで取得できる情報 
 *  	使いどころ: 基本的な情報だけで十分なとき.キャスト不要.
 *  	取得方法:Spring MVC が Authentication → principal を取り出してセットしてくれる.
 *  
 *  @AuthenticationPrincipal CustomUserDetails customUserDetails
 *  	型： CustomUserDetails
 * 		取得できる情報: username, authorities などの UserDetails インタフェースの標準メソッドで取得できる情報.また独自フィールドやメソッドも使用できる.
 * 		使いどころ: UserDetails では取得できない独自フィールドや独自メソッドを使用したいとき.キャスト不要.
 * 
 * 	Authentication authentication
 * 		型: Authentication
 * 		取得できる情報:principal, credentials, authorities, details
 * 		使いどころ: 認証情報全般が必要なとき
 * 
 * 	Principal principal
 * 		型: java.security.Principal	
 * 		取得できる情報: username のみ
 * 		使いどころ: ユーザー名だけが必要な時
 * 
 * これらはすべて 最終的に ThreadLocal の SecurityContext にある Authentication から取り出した情報.	
 *
 *@AuthenticationPrincipal と Authentication authentication のちがい
 *	@AuthenticationPrincipal は Authentication から principal だけを取り出してくれる便利機能で,
 *	Authentication は Authentication オブジェクト全体にアクセスできる.
 *
 *	ただし Authentication は UserDetailsのuserNameを取得したいとき principal を取り出す＋キャストが必要になる.
 *	つまり,
 *		// principal を取り出す ＋ キャスト
 *		CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal(); 
 *		// userNameの取得
 *    	String userName = user.getUsername();
 *	としないといけない.
 *	また authentication.getPrincipal() の戻り値が Object型なので型変換には注意が必要(instanceofでチェック)なため,
 *	少し面倒くさくなるので注意が必要.
 *
 */
