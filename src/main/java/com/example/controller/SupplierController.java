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
import com.example.domain.suppliers.model.MSupplier;
import com.example.domain.suppliers.service.SupplierService;
import com.example.form.suppliers.EditForm;
import com.example.form.suppliers.RegisterForm;
import com.github.pagehelper.PageInfo;

/**
 * 入荷先情報に関するコントローラクラス.
 * 
 * */
@Controller
@RequestMapping("/suppliers")
public class SupplierController {

	@Autowired
	private SupplierService supplierService;

	@Autowired
	private CustomHeader customHeader;

	@Autowired
	private ModelMapper modelMapper;

	// 1ページで表示する入荷先情報を10に設定する.
	private static final int SHOW_SIZE = 10;

	/**
	 * 入荷先一覧画面に遷移する.
	 * 
	 * @param model ビューにデータを渡すためのモデル.
	 * @param search 入荷先を検索するときの検索語句.
	 * @param sortItem 入荷先を並び替えるときの並び替え項目(IDまたはふりがな)
	 * @param sort 入荷先を並び替えるときの並び替え順序(昇順または降順)
	 * @param page 取得するページ番号.
	 * @return 入荷先一覧画面のビュー名.
	 */
	@GetMapping("/list")
	public String getList(Model model,
			@RequestParam(required = false) String search,
			@RequestParam(defaultValue = "id") String sortItem,
			@RequestParam(defaultValue = "asc") String sort,
			@RequestParam(defaultValue = "1") int page) {
		/* 入荷先一覧を押して遷移してきたとき,search(検索語句)はrequired = falseのためnull,
		 * sortItem(並び替え項目)とsort(並び替え順序)はdefaultValueの値がそれぞれ入っている. */

		// if文内の分岐により変数に代入する情報を決定し,modelに格納するので変数の宣言とオブジェクトの初期化をif文の前に行う.
		PageInfo<MSupplier> supplierList = null;

		// 入荷先一覧画面に遷移したとき(削除済み以外の入荷先一覧を入荷先IDの昇順で取得する).
		if (search == null) {
			supplierList = supplierService.getAllInAscById(page, SHOW_SIZE);

			/* 検索ボタン,各種昇順・降順ボタンを押したときのsearchには,検索フォームに何も入っていなければ空白が入るのでnullではないためこちらに分岐する.
			 * sortItem(並び替え項目)がidまたはfuriganaか確認する. */
		} else if (sortItem.equals("id") || sortItem.equals("furigana")) {

			// sort(並び替え順序)がascかdescか確認する.
			if (sort.equals("asc") || sort.equals("desc")) {
				supplierList = supplierService.getSearchResults(page, SHOW_SIZE, search, sortItem, sort);

				/* sort(並び替え順序)がascまたはdescでないとき(開発者ツールでクエリパラメータで値を変えられたときなど)は,
				 * 削除済み以外の入荷先情報を入荷先IDで昇順に並べた入荷先一覧を取得する. */
			} else {
				supplierList = supplierService.getAllInAscById(page, SHOW_SIZE);
				// 検索フォームに検索語句があると検索できているようにみえるためsearchに空白を入れる.
				search = "";
			}

			/* sortItem(並べ替え項目)がidやfuriganaでないとき(開発者ツールでクエリパラメータで値を変えられたときなど)は,
			 * 削除済み以外の入荷先情報を入荷先IDで昇順に並べた入荷先一覧を取得する. */
		} else {
			supplierList = supplierService.getAllInAscById(page, SHOW_SIZE);
			// 検索フォームに検索語句があると検索できているようにみえるためsearchに空白を入れる.
			search = "";
		}

		model.addAttribute("supplierList", supplierList);
		model.addAttribute("search", search);
		model.addAttribute("sortItem", sortItem);
		model.addAttribute("sort", sort);

		// ヘッダーの色と項目を設定する.
		customHeader.setRed("入荷先一覧");
		model.addAttribute("customHeader", customHeader);

		return "/suppliers/list";

	}

	/**
	 * 入荷先登録ボタンを押してくるところ.
	 * 入荷先登録フォーム画面へ遷移する.
	 * 
	 * @param model ビューにデータを渡すためのモデル.
	 * @param form 入荷先登録フォーム.
	 * @return 入荷先登録フォーム画面のビュー名.
	 */
	@GetMapping("/register")
	public String getRegister(Model model, @ModelAttribute RegisterForm form) {

		// ヘッダーの設定をmodel格納する処理をまとめたメソッドを呼び出している.(下のほうでprivateメソッドとして設定している).
		this.goToRegister(model);

		return "/suppliers/register";
	}

	/**
	 * 入荷先登録フォーム画面の登録を押してくるところ.
	 * 入荷先情報の登録内容を確認して登録する.
	 * 
	 * @param model ビューにデータを渡すためのモデル.
	 * @param form 入荷先登録フォーム.
	 * @param bindingResult バリデーションエラー.
	 * @return 	バリデーションエラーがあれば入荷先登録フォーム画面のビュー名.
	 * 			正常に完了した場合,入荷先一覧画面のビュー名(リダイレクト).
	 */
	@PostMapping("/register")
	public String postRegister(Model model, @ModelAttribute @Validated RegisterForm form,
			BindingResult bindingResult) {

		// バリデーションエラーがあれば入荷先登録フォーム画面へ戻る.
		if (bindingResult.hasErrors()) {

			// ヘッダーの設定をmodel格納する処理をまとめたメソッドを呼び出している.(下のほうでprivateメソッドとして設定している).
			this.goToRegister(model);

			return "/suppliers/register";
		}

		// formクラスをエンティティクラスに変換する.
		MSupplier supplier = modelMapper.map(form, MSupplier.class);

		// 入荷先を登録する.
		supplierService.registerOne(supplier);

		return "redirect:/suppliers/list";
	}

	/**
	 * 入荷先情報修正ボタンを押してくるところ.
	 * 入荷先情報修正フォーム画面へ遷移する.
	 * 
	 * @param model ビューにデータを渡すためのモデル.
	 * @param supplierId 修正する入荷先のID.
	 * @param form 入荷先修正フォーム.
	 * @return 	パスパラメータの入荷先IDがDBに存在しなければエラー画面のビュー名.
	 * 			正常に完了した場合,入荷先情報修正フォーム画面のビュー名.
	 */
	@GetMapping("/{supplierId}/edit")
	public String getEdit(Model model, @PathVariable Integer supplierId, @ModelAttribute EditForm form) {

		// 入荷先IDから情報を取得する(削除済みは除く).
		MSupplier supplier = supplierService.getBySupplierId(supplierId);

		// 取得した入荷先情報が存在するか確認する(存在しなければエラー画面へ).
		if (supplier == null) {
			return "/error";
		}

		// エンティティクラスをformクラスに変換し,modelに格納する.
		form = modelMapper.map(supplier, EditForm.class);
		model.addAttribute("editForm", form);

		/* 入荷先IDを渡すために入荷先情報をmodelに格納する処理,ヘッダーの設定をmodel格納する処理をまとめたメソッドを呼び出している.
		 * (下のほうでprivateメソッドとして設定している). */
		this.goToEdit(model, supplier);

		return "/suppliers/edit";
	}

	/**
	 * 入荷先情報修正フォーム画面の確定ボタンを押してくるところ.
	 * 入荷先情報の修正内容を確認して更新する.
	 * 
	 * @param model ビューにデータを渡すためのモデル.
	 * @param supplierId 修正する入荷先のID.
	 * @param form 入荷先修正フォーム.
	 * @param bindingResult バリデーションエラー.
	 * @return 	パスパラメータの入荷先IDがDBに存在しなければエラー画面のビュー名.
	 * 			バリデーションエラーがあれば入荷先情報修正フォーム画面のビュー名.
	 * 			正常に完了した場合,入荷先一覧画面のビュー名(リダイレクト).
	 */
	@PostMapping("/{supplierId}/edit")
	public String postEdit(Model model, @PathVariable Integer supplierId,
			@ModelAttribute @Validated EditForm form,
			BindingResult bindingResult) {
		// @PathVariableの引数のname属性は省略している.

		// 入荷先IDから情報を取得する(削除済みは除く).
		MSupplier supplier = supplierService.getBySupplierId(supplierId);

		// 取得した入荷先情報が存在するか確認する(存在しなければエラー画面へ).
		if (supplier == null) {
			return "/error";
		}

		// バリデーションエラーがあれば入荷先情報修正フォーム画面へ戻る.
		if (bindingResult.hasErrors()) {

			/* 入荷先IDを渡すために入荷先情報をmodelに格納する処理,ヘッダーの設定をmodel格納する処理をまとめたメソッドを呼び出している.
			 * (下のほうでprivateメソッドとして設定している). */
			this.goToEdit(model, supplier);

			return "/suppliers/edit";
		}

		// formクラスをエンティティクラスに変換する.
		MSupplier mSupplier = modelMapper.map(form, MSupplier.class);

		// 入荷先IDを設定する.
		mSupplier.setSupplierId(supplierId);

		// 入荷先情報を更新する.
		supplierService.updateOne(mSupplier);

		return "redirect:/suppliers/list";

	}

	/**
	 * 入荷先情報削除ボタンを押してくるところ.
	 * 入荷先情報削除フォーム画面へ遷移する.
	 * 
	 * @param model ビューにデータを渡すためのモデル.
	 * @param supplierId 削除する入荷先のID.
	 * @return 	パスパラメータの入荷先IDがDBに存在しなければエラー画面のビュー名.
	 * 			正常に完了した場合,入荷先情報削除フォーム画面のビュー名.
	 */
	@GetMapping("/{supplierId}/delete")
	public String getDelete(Model model, @PathVariable Integer supplierId) {

		// 入荷先IDから情報を取得する(削除済みは除く).
		MSupplier supplier = supplierService.getBySupplierId(supplierId);

		// 取得した入荷先情報が存在するか確認する(存在しなければエラー画面へ).
		if (supplier == null) {
			return "/error";
		}

		// 取得した入荷先情報を入荷先情報削除フォーム画面に渡すためmodelに格納する(supplierIdを渡すため).
		model.addAttribute("supplier", supplier);

		// ヘッダーの色と項目を設定する.
		customHeader.setRed("入荷先削除");
		model.addAttribute("customHeader", customHeader);

		return "/suppliers/delete";
	}

	/**
	 * 入荷先情報削除フォーム画面の削除ボタンを押してくるところ.
	 * 入荷先情報の削除フラグを更新(1にして論理削除)する.
	 * 
	 * @param model ビューにデータを渡すためのモデル.
	 * @param supplierId 削除する入荷先のID.
	 * @param bindingResult バリデーションエラー.
	 * @return 	パスパラメータの入荷先IDがDBに存在しなければエラー画面のビュー名.
	 * 			正常に完了した場合,入荷先一覧画面のビュー名(リダイレクト).
	 */
	@PostMapping("/{supplierId}/delete")
	public String postDelete(Model model, @PathVariable Integer supplierId) {

		// 入荷先IDから情報を取得する(削除済みは除く).
		MSupplier supplier = supplierService.getBySupplierId(supplierId);

		// 取得した入荷先情報が存在するか確認する(存在しなければエラー画面へ).
		if (supplier == null) {
			return "/error";
		}

		// 入荷先の削除フラグを削除済みに変更する.
		supplierService.updateIsDeleted(supplier);

		return "redirect:/suppliers/list";
	}

	// ==========================================
	//    privateメソッド.
	// ==========================================

	/**
	 * 入荷先情報登録フォーム画面への遷移と,
	 * 入荷先情報登録フォーム画面で確定ボタンを押した後のバリデーションエラー時にフォームに戻るときの共通処理をまとめたメソッド.
	 * 
	 * @param model ビューにデータを渡すためのモデル.
	 */
	private void goToRegister(Model model) {

		// ヘッダーの色と項目を設定する.
		customHeader.setRed("入荷先登録");
		model.addAttribute("customHeader", customHeader);

	}

	/**
	 * 入荷先情報修正フォーム画面への遷移と,
	 * 入荷先情報修正フォーム画面で確定ボタンを押した後のバリデーションエラー時にフォームに戻るときの共通処理をまとめたメソッド.
	 * 
	 * @param model ビューにデータを渡すためのモデル.
	 * @param supplier 入荷先情報.
	 */
	private void goToEdit(Model model, MSupplier supplier) {

		// 取得した入荷先情報を入荷先情報修正フォーム画面に渡すためmodelに格納する(supplierIdを渡すため).
		model.addAttribute("supplier", supplier);

		// ヘッダーの色と項目を設定する.
		customHeader.setRed("入荷先修正");
		model.addAttribute("customHeader", customHeader);
	}

}
