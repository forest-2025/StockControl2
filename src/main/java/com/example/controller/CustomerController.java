package com.example.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.example.domain.customers.model.MCustomer;
import com.example.domain.customers.service.CustomerService;
import com.example.form.customers.EditForm;
import com.example.form.customers.RegisterForm;
import com.github.pagehelper.PageInfo;

@Controller
@RequestMapping("/customers")
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private CustomHeader customHeader;

	@Autowired
	private ModelMapper modelMapper;

	// 1ページで表示する入荷先情報を10に設定する.
	private static final int SHOW_SIZE = 10;

	/** 出荷先一覧画面に遷移する. */
	@GetMapping("/list")
	public String getList(
			@RequestParam(required = false) String search,
			@RequestParam(defaultValue = "id") String sortItem,
			@RequestParam(defaultValue = "asc") String sort,
			@RequestParam(defaultValue = "1") int page,
			Model model) {

		/* 出荷先一覧を押して遷移してきたとき,search(検索語句)はrequired = falseのためnull,
		 * sortItem(並び替え項目)とsort(並び替え順序)はdefaultValueの値がそれぞれ入っている. */

		// if文内の分岐により変数に代入する情報を決定し,modelに格納するので変数の宣言とオブジェクトの初期化をif文の前に行う.
		PageInfo<MCustomer> customerList = null;

		// 出荷先一覧画面に遷移したとき(削除済み以外の出荷先一覧を出荷先IDの昇順で取得する).
		if (search == null) {
			customerList = customerService.getAllInAscById(page, SHOW_SIZE);

			/* 検索ボタン,各種昇順・降順ボタンを押したときのsearchには,検索フォームに何も入っていなければ空白が入るのでnullではないためこちらに分岐する.
			 * sortItem(並び替え項目)がidまたはfuriganaか確認する. */
		} else if (sortItem.equals("id") || sortItem.equals("furigana")) {

			// sort(並び替え順序)がascかdescか確認する.
			if (sort.equals("asc") || sort.equals("desc")) {
				customerList = customerService.getSearchResults(page, SHOW_SIZE, search, sortItem, sort);

			/* sort(並び替え順序)がascまたはdescでないとき(開発者ツールでクエリパラメータで値を変えられたときなど)は,
			 * 削除済み以外の入荷先情報を出荷先IDで昇順に並べた入荷先一覧を取得する. */
			} else {
				customerList = customerService.getAllInAscById(page, SHOW_SIZE);
				// 検索フォームに検索語句があると検索できているようにみえるためsearchに空白を入れる.
				search = "";
			}

		/* sortItem(並べ替え項目)がidやfuriganaでないとき(開発者ツールでクエリパラメータで値を変えられたときなど)は,
		 * 削除済み以外の入荷先情報を入荷先IDで昇順に並べた出荷先一覧を取得する. */
		} else {
			customerList = customerService.getAllInAscById(page, SHOW_SIZE);
			// 検索フォームに検索語句があると検索できているようにみえるためsearchに空白を入れる.
			search = "";
		}

		model.addAttribute("customerList", customerList);
		model.addAttribute("search", search);
		model.addAttribute("sortItem", sortItem);
		model.addAttribute("sort", sort);

		// ヘッダーの色と項目を設定する.
		customHeader.setBlue("出荷先一覧");
		model.addAttribute("customHeader", customHeader);

		return "/customers/list";

	}

	/** 出荷先登録ボタンを押してくるところ. */
	@GetMapping("/register")
	public String getRegister(Model model, @ModelAttribute RegisterForm form) {

		// ヘッダーの色と項目を設定する.
		customHeader.setBlue("出荷先登録");
		model.addAttribute("customHeader", customHeader);

		return "/customers/register";
	}

	/** 出荷先登録フォーム画面の登録を押してくるところ. */
	@PostMapping("/register")
	public String postRegister(Model model, @ModelAttribute @Validated RegisterForm form,
			BindingResult bindingResult) {

		// バリデーションエラーがあれば出荷先登録フォームへ戻る.
		if (bindingResult.hasErrors()) {

			// ヘッダーの色と項目を設定する.
			customHeader.setBlue("出荷先登録");
			model.addAttribute("customHeader", customHeader);

			return "/customers/register";
		}

		// formクラスをエンティティクラスに変換する.
		MCustomer customer = modelMapper.map(form, MCustomer.class);

		// 入荷先を登録する.
		customerService.registerOne(customer);

		return "redirect:/customers/list";
	}

	/** 出荷先情報修正ボタンを押してくるところ. */
	@GetMapping("/{customerId}/edit")
	public String getEdit(Model model, @PathVariable Integer customerId, @ModelAttribute EditForm form) {

		// 出荷先IDから情報を取得する(削除済み以外).
		MCustomer customer = customerService.getByCustomerId(customerId);

		// 取得した出荷先情報が存在するか確認する(存在しなければエラー画面へ).
		if (customer == null) {
			return "/error";
		}

		// エンティティクラスをformクラスに変換し,modelに格納する.
		form = modelMapper.map(customer, EditForm.class);
		model.addAttribute("editForm", form);

		/* 出荷先IDを渡すために出荷先情報をmodelに格納する処理,ヘッダーの設定をmodel格納する処理をまとめたメソッドを呼び出している.
		 * (下のほうでprivateメソッドとして設定している). */
		this.goToEdit(model, customer);

		return "/customers/edit";
	}

	/** 出荷先情報修正フォーム画面の確定ボタンを押してくるところ. */
	@PostMapping("/{customerId}/edit")
	public String postEdit(Model model, @PathVariable Integer customerId,
			@ModelAttribute @Validated EditForm form,
			BindingResult bindingResult) {
		// @PathVariableの引数のname属性は省略している.

		// 出荷先IDから情報を取得する(削除済み以外).
		MCustomer customer = customerService.getByCustomerId(customerId);

		// 取得した出荷先情報が存在するか確認する(存在しなければエラー画面へ).
		if (customer == null) {
			return "/error";
		}

		// バリデーションエラーがあれば出荷先情報修正フォーム画面へ戻る.
		if (bindingResult.hasErrors()) {

			/* 出荷先IDを渡すために出荷先情報をmodelに格納する処理,ヘッダーの設定をmodel格納する処理をまとめたメソッドを呼び出している.
			 * (下のほうでprivateメソッドとして設定している). */
			this.goToEdit(model, customer);

			return "/customers/edit";
		}

		// formクラスをエンティティクラスに変換する.
		MCustomer mCustomer = modelMapper.map(form, MCustomer.class);

		// 出荷先IDを設定する.
		mCustomer.setCustomerId(customerId);

		// 出荷先情報を更新する.
		customerService.updateOne(mCustomer);

		return "redirect:/customers/list";

	}

	/** 出荷先情報削除ボタンを押してくるところ */
	@GetMapping("/{customerId}/delete")
	public String getDelete(Model model, @PathVariable Integer customerId) {

		// 出荷先IDから情報を取得する(削除済み以外).
		MCustomer customer = customerService.getByCustomerId(customerId);

		// 取得した出荷先情報が存在するか確認する(存在しなければエラー画面へ).
		if (customer == null) {
			return "/error";
		}

		// 取得した出荷先情報を出荷先情報削除フォーム画面に渡すためmodelに格納する(customerIdを渡すため).
		model.addAttribute("customer", customer);

		// ヘッダーの色と項目を設定する.
		customHeader.setBlue("出荷先削除");
		model.addAttribute("customHeader", customHeader);

		return "/customers/delete";
	}

	/** 出荷先情報削除フォーム画面の削除ボタンを押してくるところ. */
	@PostMapping("/{customerId}/delete")
	public String postDelete(Model model, @PathVariable Integer customerId) {

		// 出荷先IDから情報を取得する(削除済み以外).
		MCustomer customer = customerService.getByCustomerId(customerId);

		// 取得した出荷先情報が存在するか確認する(存在しなければエラー画面へ).
		if (customer == null) {
			return "/error";
		}

		// 出荷先の削除フラグを削除済みに変更する.
		customerService.updateIsDeleted(customer);

		return "redirect:/customers/list";
	}

	/** 出荷先情報修正フォーム画面への遷移と出荷先情報修正フォーム画面で確定ボタンを押した後のバリデーションエラー時にフォームに戻るときの共通処理をまとめたメソッド.　*/
	private void goToEdit(Model model, MCustomer customer) {

		// 取得した出荷先情報を出荷先情報修正フォーム画面に渡すためmodelに格納する(customerIdを渡すため).
		model.addAttribute("customer", customer);

		// ヘッダーの色と項目を設定する.
		customHeader.setBlue("出荷先修正");
		model.addAttribute("customHeader", customHeader);

	}

}
