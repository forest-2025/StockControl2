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
	
	/**
	 * 認証済みユーザーの姓と名を取得する.
	 *
	 * @return ユーザーの姓と名.
	 */
	public String getFullName() {
		return familyName + firstName;
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
 * そのため独自の設定を追加したい場合(今回はユーザーの姓と名とユーザーIDを追加したい)Userクラスを拡張する.
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
 * 認証後の Authentication オブジェクトのフィールド credentials に生のパスワードがあるが,
 * こちらは UsernamePasswordAuthenticationToken クラスが拡張している AbstractAuthenticationToken クラスが,
 * CredentialsContainer を実装しているため, eraseCredentials()メソッドが自動的に削除してくれるため問題ない.
 * 
 * コントローラクラスで使用するとき
 * 
 * 	@AuthenticationPrincipal UserDetails userDetails	
 * 		型: UserDetails	
 *  	取得先: Authentication.principal
 *  	取得できる情報: username, authorities などの UserDetails インタフェースの標準メソッドで取得できる情報 
 *  	使いどころ: 基本的な情報だけで十分なとき
 *  
 *  @AuthenticationPrincipal CustomUserDetails customUserDetails
 *  	型： CustomUserDetails
 * 		取得先： Authentication.principal
 * 		取得できる情報: username, authorities などの UserDetails インタフェースの標準メソッドで取得できる情報.また独自フィールドやメソッドも使用できる.
 * 		使いどころ: UserDetails では取得できない独自フィールドや独自メソッドを使用したいとき
 * 
 * 	Authentication authentication
 * 		型: Authentication
 * 		取得先: SecurityContextHolder.getContext().getAuthentication()
 * 		取得できる情報:principal, credentials, authorities, details
 * 		使いどころ: 認証情報全般が必要なとき
 * 
 * 	Principal principal
 * 		型: java.security.Principal	
 * 		取得先: Authentication.principal (ラップ)
 * 		取得できる情報: username のみ
 * 		使いどころ: ユーザー名だけが必要な時
 * 		
 *
 *@AuthenticationPrincipal と Authentication authentication のちがい
 *	@AuthenticationPrincipal は Authentication から principal だけを取り出してくれる便利機能で,
 *	Authentication は Authentication オブジェクト全体にアクセスできる.
 *	ただし Authentication は UserDetailsのuserNameを取得したいとき principal を取り出す＋キャストが必要になる.
 *	つまり,
 *		CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal(); // ← キャスト必要
 *    	String userName = user.getUsername();
 *	としないといけない,また　authentication.getPrincipal() の戻り値が Object型のため,型変換には注意が必要(instanceofでチェック)なため,
 *	少し面倒くさい.
 *書き方	取得先 / 経路	取得される情報	誰が取りに行く	特徴 / 使いどころ
① SecurityContextHolder.getContext().getAuthentication()	SecurityContextHolder → Authentication → principal	principal（UserDetailsなど）、credentials、authorities、details	自分（開発者）	低レベル。フル情報が欲しい場合。キャストが必要。
② 引数 Authentication authentication	Spring MVC が SecurityContextHolder から取得して引数にセット	principal、credentials、authorities、details	Spring が取り出して渡す	コントローラでフル情報が欲しい場合。よく使う。
③ @AuthenticationPrincipal UserDetails userDetails	Spring MVC が Authentication → principal を取り出してセット	UserDetails 標準メソッド（username, authorities など）	Spring が principal を取り出して渡す	標準情報だけで十分な場合。キャスト不要。
④ @AuthenticationPrincipal CustomUserDetails customUserDetails	Spring MVC が Authentication → principal を取り出してセット	principal の内容 + 独自フィールド（例: userId）	Spring が principal を取り出して渡す	独自ユーザー情報を使いたい場合。キャスト不要。
⑤ Principal principal（java.security.Principal）	Servlet が Authentication.principal をラップして返す	username のみ	Servlet がラップして渡す	ユーザー名だけ必要な場合。抽象度が高い。
🔹 内部イメージ
SecurityContextHolder (ThreadLocal)
 └─ SecurityContext
     └─ Authentication
         ├─ principal (UserDetails / CustomUserDetails)
         ├─ credentials (生パスワード)
         ├─ authorities
         └─ details

① 直接取得  → SecurityContextHolder から自分で取る
② Authentication引数 → Spring が SecurityContextHolder から取って渡す
③ @AuthenticationPrincipal → Spring が Authentication.getPrincipal() を取り出して渡す
④ Principal → Servlet が principal.getName() だけ抽象化して渡す
🔹 ポイントまとめ
情報量の多さ
①②：フル（principal, credentials, authorities, details）
③④：principalのみ（UserDetails標準メソッドや独自フィールドは③でアクセス可）
⑤：usernameだけ
誰が取り出すか
①：自分
②③：Spring
⑤：Servlet
便利さ
③や④はキャスト不要でシンプル
標準情報だけでよければ③
独自フィールドも使いたければ④
認証情報全般が必要なら①②

💡 一言でまとめると：

Authentication が本体で、@AuthenticationPrincipal や Principal は「Spring/Spring MVC/Servlet が渡してくれる便利な窓口」
 * 
 */
