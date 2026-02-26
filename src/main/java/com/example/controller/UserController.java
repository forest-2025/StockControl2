package com.example.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.component.CustomHeader;
import com.example.domain.users.model.MUser;
import com.example.domain.users.service.UserService;
import com.example.form.users.EditForm;
import com.example.form.users.PasswordEditForm;
import com.example.form.users.RegisterForm;
import com.example.validation.GroupOrder;
import com.github.pagehelper.PageInfo;

/** 
 * ユーザーの情報に関するコントローラクラス.
 * 
 *  */
@Controller
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private CustomHeader customHeader;

	@Autowired
	private ModelMapper modelMapper;

	/**
	 * ユーザー一覧画面に遷移する.
	 * 
	 * @param model ビューにデータを渡すためのモデル.
	 * @param search ユーザーを検索するときの検索語句.
	 * @param page 取得するページ番号.
	 * @return ユーザー一覧画面のビュー名.
	 */
	@GetMapping("/list")
	public String getList(Model model,
			@RequestParam(required = false) String search,
			@RequestParam(defaultValue = "asc") String sort,
			@RequestParam(defaultValue = "1") int page) {

		/* @RequestParamのrequired属性をfalseにすることで検索パラメータ（URLの末尾の？に続く変数）の,
		 * パラメータ名searchがあってもなくても受け付けられるようにしている.
		 * パラメータ名searchが無ければ削除されていない全ユーザーの一覧を取得し,あればsearchの
		 * 値が含まれるユーザーを検索する.
		 * @RequestParam(defaultValue = "1") int pageはpage=1がデフォルト. */

		PageInfo<MUser> userList = userService.findAllSorted(search, sort, page);
		model.addAttribute("userList", userList);
		model.addAttribute("search", search);

		// ヘッダーの色と項目を設定する.
		customHeader.setYellow("ユーザー一覧");
		model.addAttribute("customHeader", customHeader);

		return "/users/list";
	}

	/**
	 * ユーザー登録ボタンを押してくるところ.
	 * ユーザー登録フォーム画面へ遷移する.
	 * 
	 * @param model ビューにデータを渡すためのモデル.
	 * @param form ユーザー登録フォーム.
	 * @return ユーザー登録フォーム画面のビュー名.
	 */
	@GetMapping("/register")
	public String getRegister(Model model, @ModelAttribute RegisterForm form) {

		// ヘッダーの設定をmodel格納する処理をまとめたメソッドを呼び出している.(下のほうでprivateメソッドとして設定している).
		this.goToRegister(model);

		return "/users/register";
	}

	/**
	 * ユーザー登録フォーム画面の登録を押してくるところ.
	 * ユーザー情報の登録内容を確認して登録する.
	 * 
	 * @param model ビューにデータを渡すためのモデル.
	 * @param form ユーザー登録フォーム.
	 * @param bindingResult バリデーションエラー.
	 * @return 	バリデーションエラーがあればユーザー登録フォーム画面のビュー名.
	 * 			正常に完了した場合,ユーザー一覧画面のビュー名(リダイレクト).
	 */
	@PostMapping("/register")
	public String postRegister(Model model, @ModelAttribute @Validated(GroupOrder.class) RegisterForm form,
			BindingResult bindingResult) {

		// バリデーションエラーがあればユーザー登録フォーム画面へ戻る.
		if (bindingResult.hasErrors()) {

			// ヘッダーの設定をmodel格納する処理をまとめたメソッドを呼び出している.(下のほうでprivateメソッドとして設定している).
			this.goToRegister(model);

			return "/users/register";
		}

		// formクラスをエンティティクラスに変換する.
		MUser user = modelMapper.map(form, MUser.class);

		// ユーザーを登録する.
		userService.registerOne(user);

		return "redirect:/users/list";
	}

	/**
	 * ユーザー情報修正ボタンを押してくるところ.
	 * ユーザー情報修正フォーム画面へ遷移する.
	 * 
	 * @param model ビューにデータを渡すためのモデル.
	 * @param userId ユーザー情報を修正するユーザーのID.
	 * @param form ユーザー修正フォーム.
	 * @return 	パスパラメータのユーザーIDがDBに存在しなければエラー画面のビュー名.
	 * 			正常に完了した場合,ユーザー情報修正フォーム画面のビュー名.
	 */
	@GetMapping("/{userId}/edit")
	public String getEdit(Model model, @PathVariable Integer userId, @ModelAttribute EditForm form) {

		// ユーザーIDから情報を取得する.
		MUser user = userService.getByUserId(userId);

		// 取得したユーザー情報が存在するか確認する(存在しなければエラー画面へ).
		if (user == null) {
			return "/error";
		}

		// エンティティクラスをformクラスに変換し,modelに格納する.
		form = modelMapper.map(user, EditForm.class);
		model.addAttribute("editForm", form);

		/* ユーザーIDを渡すためにユーザー情報をmodelに格納する処理,ヘッダーの設定をmodel格納する処理をまとめたメソッドを呼び出している.
		 * (下のほうでprivateメソッドとして設定している). */
		this.goToEdit(model, user);

		return "/users/edit";
	}

	/**
	 * ユーザー情報修正フォーム画面の確定ボタンを押してくるところ.
	 * ユーザー情報の修正内容を確認して更新する.
	 * 
	 * @param model ビューにデータを渡すためのモデル.
	 * @param userId ユーザー情報を修正するユーザーのID.
	 * @param userDetails ログイン中のユーザーの情報.
	 * @param request HTTPリクエストをJavaで扱いやすい形にしたHttpServletRequestインターフェースの実装オブジェクト.
	 * @param response HTTPレスポンスをJavaで扱いやすい形にしたHttpServletResponseインターフェースの実装オブジェクト.
	 * @param authentication Authenticationインターフェースを実装したオブジェクトで,認証状態を保持する(ユーザー情報・権限・認証済みか等).
	 * @param form 入荷フォーム.
	 * @param bindingResult バリデーションエラー.
	 * @return 	パスパラメータのユーザーIDがDBに存在しなければエラー画面のビュー名.
	 * 			バリデーションエラーがあればユーザー情報修正フォーム画面のビュー名.
	 * 			ユーザーが自身の管理者権限を管理者から一般に変更した場合,ユーザーに関する情報の閲覧権限を失うためログアウト画面のビュー名(リダイレクト).
	 * 			正常に完了した場合,ユーザー一覧画面のビュー名(リダイレクト).
	 */
	@PostMapping("/{userId}/edit")
	public String postEdit(Model model, @PathVariable Integer userId,
			@AuthenticationPrincipal UserDetails userDetails,
			HttpServletRequest request,
			HttpServletResponse response,
			Authentication authentication,
			@ModelAttribute @Validated(GroupOrder.class) EditForm form,
			BindingResult bindingResult) {
		// @PathVariableの引数のname属性は省略している.

		// ユーザーIDから情報を取得する.
		MUser user = userService.getByUserId(userId);

		// 取得したユーザー情報が存在するか確認する(存在しなければエラー画面へ).
		if (user == null) {
			return "/error";
		}

		// バリデーションエラーがあればユーザー情報修正フォーム画面へ戻る.
		if (bindingResult.hasErrors()) {

			/* ユーザーIDを渡すためにユーザー情報をmodelに格納する処理,ヘッダーの設定をmodel格納する処理をまとめたメソッドを呼び出している.
			 * (下のほうでprivateメソッドとして設定している). */
			this.goToEdit(model, user);

			return "/users/edit";
		}

		// formクラスをエンティティクラスに変換する.
		MUser mUser = modelMapper.map(form, MUser.class);

		// ユーザーIDを設定する.
		mUser.setUserId(userId);

		// ユーザー情報を更新する.
		userService.updateExceptPassword(mUser);

		// ログインユーザーが自身の管理者権限を管理者から一般に変更した場合はリダイレクト先のユーザー情報一覧について閲覧権限がなくなるためログアウトさせる.
		if (userDetails.getUsername().equals(user.getEmailAddress()) && user.getIsAdmin() == 1
				&& mUser.getIsAdmin() == 0) {

			/* SecurityContextLogoutHandlerはSpringSecurityのログアウト用のユーティリティクラス(補助クラス)で,
			 * ユーザーをログアウトさせる処理を簡単に行える. */
			SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();

			/* スレッドはプログラムを実行する作業単位のことで(1リクエスト = 1スレッド,オブジェクトでもある),
			 * その作業を行う専用の場所がThreadLocal(オブジェクト)でJVMのヒープメモリ上に保存される.
			 * そのThreadLocalの中にSecurityContextというコンテナがあり(インターフェースなので実際は実装したオブジェクトが入っている),
			 * そのコンテナの中にAuthenticationというユーザーのユーザー名・権限・認証をもつオブジェクト(インターフェースなので実際は実装したオブジェクトが入っている)
			 * があり,そのAuthenticationの中のフィールドprincipalがユーザーの詳細情報を保持しているUserDetailsオブジェクト.
			 * (インターフェースなので実際は実装したオブジェクトが入っている).
			 * これは現在のリクエスト用で,次回のリクエスト用にHTTPセッションにも同じSecurityContextを保存する.
			 * (SPRING_SECURITY_CONTEXTというキー名で保存).
			 * このような関係があり,SecurityContextLogoutHandlerのlogoutメソッドがSecurityContextを取得しクリアすることで,
			 * その中に保持されていたAuthentication(principal(UserDetails)を含む)への参照がなくなり,
			 * SpringSecurityから見て「ログインユーザーが存在しない状態」になる.
			 * logoutメソッドは内部的にsession.invalidate();が実行され,これによりHTTPセッションが破棄される.
			 * またCookie(JSESSIONID)を削除してくれる.
			 * 
			 *  */
			logoutHandler.logout(request, response, authentication);

			// Cookieを削除する(下にあるprivateメソッド).
			this.deleteCookie(response, "JSESSIONID"); // 上のlogout()でも削除してくれているが念のため.
			this.deleteCookie(response, "remember-me");

			return "redirect:/logout";
		}

		return "redirect:/users/list";

	}

	/**
	 * パスワード修正ボタンを押してくるところ.
	 * パスワード修正フォーム画面へ遷移する.
	 * 
	 * @param model ビューにデータを渡すためのモデル.
	 * @param userId パスワードを修正するユーザーのID.
	 * @param form パスワード修正フォーム.
	 * @return 	パスパラメータのユーザーIDがDBに存在しなければエラー画面のビュー名.
	 * 			正常に完了した場合,パスワード修正フォーム画面のビュー名.
	 */
	@GetMapping("/{userId}/passwordEdit")
	public String getPasswordEdit(Model model, @PathVariable Integer userId,
			@ModelAttribute PasswordEditForm form) {

		// ユーザーIDから情報を取得する.
		MUser user = userService.getByUserId(userId);

		// 取得したユーザー情報が存在するか確認する(存在しなければエラー画面へ).
		if (user == null) {
			return "/error";
		}

		/* ユーザーIDを渡すためにユーザー情報をmodelに格納する処理,ヘッダーの設定をmodel格納する処理をまとめたメソッドを呼び出している.
		 * (下のほうでprivateメソッドとして設定している). */
		this.goToPasswordEdit(model, user);

		return "/users/password-edit";
	}

	/**
	 * パスワード修正フォーム画面の確定ボタンを押してくるところ.
	 * パスワードの修正内容を確認して更新する.
	 * 
	 * @param model ビューにデータを渡すためのモデル.
	 * @param userId パスワードを修正するユーザーのID.
	 * @param form パスワード修正フォーム.
	 * @param bindingResult バリデーションエラー.
	 * @return 	パスパラメータのユーザーIDがDBに存在しなければエラー画面のビュー名.
	 * 			バリデーションエラーがあればパスワード修正フォーム画面のビュー名.
	 * 			正常に完了した場合,ユーザー一覧画面のビュー名(リダイレクト).
	 */
	@PostMapping("/{userId}/passwordEdit")
	public String postPasswordEdit(Model model, @PathVariable Integer userId,
			@ModelAttribute @Validated(GroupOrder.class) PasswordEditForm form,
			BindingResult bindingResult) {
		// @PathVariableの引数のname属性は省略している.

		// ユーザーIDから情報を取得する.
		MUser user = userService.getByUserId(userId);

		// 取得したユーザー情報が存在するか確認する(存在しなければエラー画面へ).
		if (user == null) {
			return "/error";
		}

		// バリデーションエラーがあればパスワード修正フォーム画面へ戻る.
		if (bindingResult.hasErrors()) {

			/* ユーザーIDを渡すためにユーザー情報をmodelに格納する処理,ヘッダーの設定をmodel格納する処理をまとめたメソッドを呼び出している.
			 * (下のほうでprivateメソッドとして設定している). */
			this.goToEdit(model, user);

			return "/users/password-edit";
		}

		// formクラスをエンティティクラスに変換する.
		MUser muser = modelMapper.map(form, MUser.class);
		// ユーザーIDを設定する.
		muser.setUserId(userId);

		// ユーザーのパスワードを更新する.
		userService.updatePassword(muser);

		return "redirect:/products/info/list";

	}

	/**
	 * ユーザー情報削除ボタンを押してくるところ.
	 * ユーザー情報削除フォーム画面へ遷移する.
	 * 
	 * @param model ビューにデータを渡すためのモデル.
	 * @param userId ユーザー情報を削除するユーザーのID.
	 * @return 	パスパラメータのユーザーIDがDBに存在しなければエラー画面のビュー名.
	 * 			正常に完了した場合ユーザー情報削除フォーム画面のビュー名.
	 */
	@GetMapping("/{userId}/delete")
	public String getDelete(Model model, @PathVariable Integer userId) {

		// ユーザーIDから情報を取得する.
		MUser user = userService.getByUserId(userId);

		// 取得したユーザー情報が存在するか確認する(存在しなければエラー画面へ).
		if (user == null) {
			return "/error";
		}

		// 取得したユーザー情報をユーザー情報修正画面に渡すためmodelに格納する(userIdを渡すため).
		model.addAttribute("user", user);

		// ヘッダーの色と項目を設定する.
		customHeader.setYellow("ユーザー削除");
		model.addAttribute("customHeader", customHeader);

		return "/users/delete";
	}

	/**
	 * ユーザー情報削除フォーム画面の削除ボタンを押してくるところ.
	 * ユーザー情報の削除フラグを更新(1にして論理削除)する.
	 * 
	 * @param model ビューにデータを渡すためのモデル.
	 * @param userId ユーザー情報を削除するユーザーのID.
	 * @param userDetails ログイン中のユーザーの情報.
	 * @param request HTTPリクエストをJavaで扱いやすい形にしたHttpServletRequestインターフェースの実装オブジェクト.
	 * @param response HTTPレスポンスをJavaで扱いやすい形にしたHttpServletResponseインターフェースの実装オブジェクト.
	 * @param authentication Authenticationインターフェースを実装したオブジェクトで,認証状態を保持する(ユーザー情報・権限・認証済みか等).
	 * @return 	パスパラメータのユーザーIDがDBに存在しなければエラー画面のビュー名.
	 * 			ログインユーザーが自身のユーザー情報を削除した場合,アプリを使用する権限を失うためログアウト画面のビュー名(リダイレクト).
	 * 			正常に完了した場合,ユーザー一覧画面のビュー名(リダイレクト).
	 */

	@PostMapping("/{userId}/delete")
	public String postDelete(Model model, @PathVariable Integer userId,
			@AuthenticationPrincipal UserDetails userDetails,
			HttpServletRequest request,
			HttpServletResponse response,
			Authentication authentication) {

		// ユーザーIDから情報を取得する.
		MUser user = userService.getByUserId(userId);

		// 取得したユーザー情報が存在するか確認する(存在しなければエラー画面へ).
		if (user == null) {
			return "/error";
		}

		// ユーザーの削除フラグを削除済みに変更する.
		userService.updateIsDeleted(user);

		// ログインユーザーが自身のユーザー情報を削除させた場合,アプリを使用する権限を失うためログアウトさせる.
		if (userDetails.getUsername().equals(user.getEmailAddress())) {

			/* SecurityContextLogoutHandlerはSpringSecurityのログアウト用のユーティリティクラス(補助クラス)で,
			 * ユーザーをログアウトさせる処理を簡単に行える. */
			SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();

			// 認証情報のクリア・セッション破棄・Cookieの削除を行い,ユーザーをログアウト状態にする.
			logoutHandler.logout(request, response, authentication);

			// Cookieを削除する(下にあるprivateメソッド).
			this.deleteCookie(response, "JSESSIONID"); // 上のlogout()でも削除してくれているが念のため.
			this.deleteCookie(response, "remember-me");

			return "redirect:/logout";
		}

		return "redirect:/users/list";
	}

	// ==========================================
	//    privateメソッド.
	// ==========================================

	/**
	 * ユーザー情報登録フォーム画面への遷移と,
	 * ユーザー情報登録フォーム画面で確定ボタンを押した後のバリデーションエラー時にフォームに戻るときの共通処理をまとめたメソッド.
	 * 
	 * @param model ビューにデータを渡すためのモデル.
	 */
	private void goToRegister(Model model) {

		// ヘッダーの色と項目を設定する.
		customHeader.setYellow("ユーザー登録");
		model.addAttribute("customHeader", customHeader);

	}

	/**
	 * ユーザー情報修正フォーム画面への遷移と,
	 * ユーザー情報修正フォーム画面で確定ボタンを押した後のバリデーションエラー時にフォームに戻るときの共通処理をまとめたメソッド.
	 * 
	 * @param model ビューにデータを渡すためのモデル.
	 * @param user ユーザー情報を修正するユーザーの情報.
	 */
	private void goToEdit(Model model, MUser user) {

		// 取得したユーザー情報をユーザー情報修正フォーム画面に渡すためmodelに格納する(userIdを渡すため).
		model.addAttribute("user", user);

		// ヘッダーの色と項目を設定する.
		customHeader.setYellow("ユーザー修正");
		model.addAttribute("customHeader", customHeader);
	}

	/**
	 * パスワード修正フォーム画面への遷移と,
	 * パスワード修正フォーム画面で確定ボタンを押した後のバリデーションエラー時にフォームに戻るときの共通処理をまとめたメソッド.
	 * 
	 * @param model ビューにデータを渡すためのモデル.
	 * @param user パスワードを修正するユーザーの情報.
	 */
	private void goToPasswordEdit(Model model, MUser user) {

		// 取得したユーザー情報をパスワード修正フォーム画面に渡すためmodelに格納する(userIdを渡すため).
		model.addAttribute("user", user);

		// ヘッダーの色と項目を設定する.
		customHeader.setYellow("パスワード修正");
		model.addAttribute("customHeader", customHeader);
	}

	/**
	 * Cookieを削除するメソッド.
	 * 
	 * @param response HTTPレスポンスをJavaで扱いやすい形にしたHttpServletResponseインターフェースの実装オブジェクト.
	 * @param name 削除したいCookieの名前.
	 */
	private void deleteCookie(HttpServletResponse response, String name) {
		/* Cookieはブラウザに保存される小さなデータで,サーバーはそれを送ったり受け取ったりしてユーザーの状態（ログイン状態や設定など）を管理する.
		 * Cookieは名前(Name) + パス (Path) + ドメイン (Domain) で識別される.
		 * 削除したいCookieと同じ名前・Path・Domainを指定して値をnullにし,setMaxAge(0)を設定すると,ブラウザはそのCookieを即座に削除する.
		 * (PathはどのURLにそのCookieを送るかをブラウザに指示するスコープで"/"に設定するとサイト全体（ルート配下のすべてのURL）が対象になる.
		 * ()で指定した秒数だけクッキーがブラウザに保存（保持）されるため,0だと即座に無効化(削除)される).
		 * 最後にresponse.addCookie(cookie)で,この削除指示をレスポンスに追加する.
		 * (まだ削除されない.削除する設定をレスポンスに設定しただけ.) */
		Cookie cookie = new Cookie(name, null);
		cookie.setPath("/");
		cookie.setMaxAge(0);
		response.addCookie(cookie);
	}

}
