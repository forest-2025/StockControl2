package com.example.controller;

import java.util.List;

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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private CustomHeader customHeader;

	@Autowired
	private ModelMapper modelMapper;

	/** ユーザー一覧画面に遷移する. */
	@GetMapping("/list")
	public String getList(@RequestParam(required = false) String search, Model model) {

		/*@RequestParamのrequired属性をfalseにすることで検索パラメータ（URLの末尾の？に続く変数）の,
		 * パラメータ名searchがあってもなくても受け付けられるようにしている.
		 * パラメータ名searchが無ければ削除されていない全ユーザーの一覧を取得し,あればsearchの値が含まれるユーザーを検索する.*/

		// 削除済み以外のユーザー情報を全件(またはsearch(検索語句)が一致するデータを)従業員番号の昇順で取得する.
		if (search == null) {
			List<MUser> userList = userService.getAll();
			model.addAttribute("userList", userList);
		} else {
			List<MUser> userList = userService.getSearchUserList(search);
			model.addAttribute("userList", userList);
			model.addAttribute("search", search);
		}

		// ヘッダーの色と項目を設定する.
		customHeader.setYellow("ユーザー一覧");
		model.addAttribute("customHeader", customHeader);

		return "/users/list";
	}

	/** ユーザー登録ボタンを押してくるところ. */
	@GetMapping("/register")
	public String getRegister(Model model, @ModelAttribute RegisterForm form) {

		// ヘッダーの色と項目を設定する.
		customHeader.setYellow("ユーザー登録");
		model.addAttribute("customHeader", customHeader);
		return "/users/register";
	}

	/** ユーザー登録フォーム画面の登録を押してくるところ. */
	@PostMapping("/register")
	public String postRegister(Model model, @ModelAttribute @Validated RegisterForm form,
			BindingResult bindingResult) {

		// 従業員番号がnullや空白でないかを確認する.
		String employeeNumber = form.getEmployeeNumber();
		if (employeeNumber == null || employeeNumber.equals("")) {
			// nullまたは空白ならif文を抜けて@NotBlankのエラーメッセージが表示されるのでなにもしない.
		} else {
			boolean isNotDuplicate = userService.isNotDuplicateEmployeeNumber(employeeNumber);
			// 登録済みの従業員番号と重複しないか確認する. 
			if (isNotDuplicate) {
				// trueなら従業員番号に重複がないのでなにもしない.
			} else {
				// falseなら従業員番号に重複があるのでエラーとエラーメッセージを追加する.
				bindingResult.rejectValue("employeeNumber", "DuplicateEmployeeNumber");
			}
		}

		// メールアドレスがnullや空白でないかを確認する.
		String emailAddress = form.getEmailAddress();
		if (emailAddress == null || emailAddress.equals("")) {
			// nullまたは空白ならif文を抜けて@NotBlankのエラーメッセージが表示されるのでなにもしない.
		} else {
			boolean isNotDuplicate = userService.isNotDuplicateEmailAddress(emailAddress);
			// 登録済みのメールアドレスと重複しないか確認する. 
			if (isNotDuplicate) {
				// trueならメールアドレスに重複がないのでなにもしない.
			} else {
				// falseならメールアドレスに重複があるのでエラーとエラーメッセージを追加する.
				bindingResult.rejectValue("emailAddress", "DuplicateEmailAddress");
			}
		}

		// バリデーションエラーがあればユーザー登録フォーム画面へ戻る.
		if (bindingResult.hasErrors()) {

			// ヘッダーの色と項目を設定する.
			customHeader.setYellow("ユーザー登録");
			model.addAttribute("customHeader", customHeader);
			return "/users/register";
		}

		// formクラスをエンティティクラスに変換する.
		MUser user = modelMapper.map(form, MUser.class);

		// ユーザーを登録する.
		userService.registerOne(user);

		return "redirect:/users/list";
	}

	/** ユーザー情報修正ボタンを押してくるところ */
	@GetMapping("/{userId}/edit")
	public String getEdit(Model model, @PathVariable Integer userId, @ModelAttribute EditForm form) {

		// ユーザーIDから情報を取得する(削除済みは除く).
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

	/** ユーザー情報修正フォーム画面の確定ボタンを押してくるところ. */
	@PostMapping("/{userId}/edit")
	public String postEdit(Model model, @PathVariable Integer userId,
			@AuthenticationPrincipal UserDetails userDetails,
			Authentication authentication,
			HttpServletRequest request,
			HttpServletResponse response,
			@ModelAttribute @Validated EditForm form,
			BindingResult bindingResult) {
		// @PathVariableの引数のname属性は省略している.

		// ユーザーIDから情報を取得する(削除済みは除く).
		MUser user = userService.getByUserId(userId);

		// 取得したユーザー情報が存在するか確認する(存在しなければエラー画面へ).
		if (user == null) {
			return "/error";
		}

		// 従業員番号がnullや空白でないかを確認する.
		String employeeNumber = form.getEmployeeNumber();
		if (employeeNumber == null || employeeNumber.equals("")) {
			// nullまたは空白ならif文を抜けて@NotBlankのエラーメッセージが表示されるのでなにもしない.
		} else {
			boolean isNotDuplicate = userService.isNotDuplicateEmployeeNumber(employeeNumber);
			// 登録済みの従業員番号と重複しないか確認する. 
			if (isNotDuplicate) {
				// trueなら従業員番号に重複がないのでなにもしない.
			} else if (employeeNumber.equals(user.getEmployeeNumber())) {
				// 重複があっても,もとの従業員番号と一緒ならtrueで従業員番号に変更がなかっただけなのでなにもしない.
			} else {
				// falseなら従業員番号に重複があるのでエラーとエラーメッセージを追加する.
				bindingResult.rejectValue("employeeNumber", "DuplicateEmployeeNumber");
			}
		}

		// メールアドレスがnullや空白でないかを確認する.
		String emailAddress = form.getEmailAddress();
		if (emailAddress == null || emailAddress.equals("")) {
			// nullまたは空白ならif文を抜けて@NotBlankのエラーメッセージが表示されるのでなにもしない.
		} else {
			boolean isNotDuplicate = userService.isNotDuplicateEmailAddress(emailAddress);
			// 登録済みのメールアドレスと重複しないか確認する. 
			if (isNotDuplicate) {
				// trueならメールアドレスに重複がないのでなにもしない.
			} else if (emailAddress.equals(user.getEmailAddress())) {
				// 重複があっても,もとのメールアドレスと一緒ならtrueでメールアドレスに変更がなかっただけなのでなにもしない.
			} else {
				// falseならメールアドレスに重複があるのでエラーとエラーメッセージを追加する.
				bindingResult.rejectValue("emailAddress", "DuplicateEmailAddress");
			}
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

			// 認証情報のクリア・セッション破棄・Cookieの削除を行い,ユーザーをログアウト状態にする.
			logoutHandler.logout(request, response, authentication);

			return "redirect:/logout";
		}

		return "redirect:/users/list";

	}

	/** パスワード修正ボタンを押してくるところ */
	@GetMapping("/{userId}/passwordEdit")
	public String getPasswordEdit(Model model, @PathVariable Integer userId, @ModelAttribute PasswordEditForm form) {

		// ユーザーIDから情報を取得する(削除済みは除く).
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

	/** パスワード修正フォーム画面の確定ボタンを押してくるところ. */
	@PostMapping("/{userId}/passwordEdit")
	public String postPasswordEdit(Model model, @PathVariable Integer userId,
			@ModelAttribute @Validated PasswordEditForm form,
			BindingResult bindingResult) {
		// @PathVariableの引数のname属性は省略している.

		// ユーザーIDから情報を取得する(削除済みは除く).
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

		return "redirect:/users/list";

	}

	/** ユーザー情報削除ボタンを押してくるところ. */
	@GetMapping("/{userId}/delete")
	public String getDelete(Model model, @PathVariable Integer userId) {

		// ユーザーIDから情報を取得する(削除済みは除く).
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

	/** ユーザー情報削除フォーム画面の削除ボタンを押してくるところ. */
	@PostMapping("/{userId}/delete")
	public String postDelete(Model model, 
			@PathVariable Integer userId,
			@AuthenticationPrincipal UserDetails userDetails,
			Authentication authentication,
			HttpServletRequest request,
			HttpServletResponse response) {

		// ユーザーIDから情報を取得する(削除済みは除く).
		MUser user = userService.getByUserId(userId);

		// 取得したユーザー情報が存在するか確認する(存在しなければエラー画面へ).
		if (user == null) {
			return "/error";
		}

		// ユーザーの削除フラグを削除済みに変更する.
		userService.updateIsDeleted(user);

		// ログインユーザーが自身のユーザー情報を削除した場合はアプリの使用ができなくなるためログアウトさせる.
		if (userDetails.getUsername().equals(user.getEmailAddress())) {

			/* SecurityContextLogoutHandlerはSpringSecurityのログアウト用のユーティリティクラス(補助クラス)で,
			 * ユーザーをログアウトさせる処理を簡単に行える. */
			SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();

			// 認証情報のクリア・セッション破棄・Cookieの削除を行い,ユーザーをログアウト状態にする.
			logoutHandler.logout(request, response, authentication);

			return "redirect:/logout";
		}

		return "redirect:/users/list";
	}

	/** ユーザー情報修正フォーム画面への遷移とユーザー情報修正フォーム画面で確定ボタンを押した後のバリデーションエラー時にフォームに戻るときの共通処理をまとめたメソッド.　*/
	private void goToEdit(Model model, MUser user) {

		// 取得したユーザー情報をユーザー情報修正フォーム画面に渡すためmodelに格納する(userIdを渡すため).
		model.addAttribute("user", user);

		// ヘッダーの色と項目を設定する.
		customHeader.setYellow("ユーザー修正");
		model.addAttribute("customHeader", customHeader);
	}

	/** パスワード修正フォーム画面への遷移とパスワード修正フォーム画面で確定ボタンを押した後のバリデーションエラー時にフォームに戻るときの共通処理をまとめたメソッド.　*/
	private void goToPasswordEdit(Model model, MUser user) {

		// 取得したユーザー情報をパスワード修正フォーム画面に渡すためmodelに格納する(userIdを渡すため).
		model.addAttribute("user", user);

		// ヘッダーの色と項目を設定する.
		customHeader.setYellow("パスワード修正");
		model.addAttribute("customHeader", customHeader);
	}

}
