package com.example.controller;

import java.util.ArrayList;
import java.util.List;

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

@Controller
@RequestMapping("/suppliers")
public class SupplierController {

	@Autowired
	private SupplierService supplierService;

	@Autowired
	private CustomHeader customHeader;

	@Autowired
	private ModelMapper modelMapper;

	/** 入荷先一覧画面に遷移する. */
	@GetMapping("/list")
	public String getList(
			@RequestParam(required = false) String sortItem,
			@RequestParam(required = false) String sort,
			@RequestParam(required = false) String search,
			Model model) {

		// if文内の分岐により変数に代入する情報を決定し,modelに格納するので変数の宣言とオブジェクトの初期化をif文の前に行う.
		List<MSupplier> supplierList = new ArrayList<>();

		// 入荷先一覧画面に遷移したとき(sortItem・sort・searchはnullになる.削除済み以外の入荷先一覧を入荷先IDの昇順で取得する).
		if (search == null) {
			supplierList = supplierService.getAllInAscById();
			
		// 検索ボタンまたは各種昇順・降順ボタンを押したとき.
		} else {
			// 並び替え項目(sortItem)がnullかどうかで分岐する.
			if (sortItem == null) {
				
				/* 検索ボタンを押した状態なので、検索語句で検索した情報を入荷先IDで昇順に並べられるように並び替え項目と順序を設定し,
				 * 削除済み以外の入荷先一覧を取得する. */
				sortItem = "id";
				sort = "asc";
				supplierList = supplierService.getSearchResults(search, sortItem, sort);
				
			// 並び替え項目がnullでなくidまたはfuriganaのときで分岐する.
			} else if (sortItem.equals("id") || sortItem.equals("furigana")) {
				
				/* 並び替え順序(sort)がascかdescならそれらの並び替え項目と順序の条件で削除済み以外の入荷先一覧を取得する.
				 * (searchは検索語句または空文字が入っているので何もしない). */
				if (sort.equals("asc") || sort.equals("desc")) {
					supplierList = supplierService.getSearchResults(search, sortItem, sort);
					
				// 並び替え順序がascまたはdescでないときは削除済み以外の入荷先情報を入荷先IDで昇順に並べた入荷先一覧を取得する.
				} else {
					supplierList = supplierService.getAllInAscById();
				}
				
			// 並び替え項目がnullでなくidまたはfuriganaでもないときは削除済み以外の入荷先情報を入荷先IDで昇順に並べた入荷先一覧を取得する.
			} else {
				supplierList = supplierService.getAllInAscById();
			
			}
		}

		model.addAttribute("supplierList", supplierList);
		model.addAttribute("search", search);

		// ヘッダーの色と項目を設定する.
		customHeader.setRed("入荷先一覧");
		model.addAttribute("customHeader", customHeader);

		return "/suppliers/list";

	}

	/** 入荷先登録ボタンを押してくるところ. */
	@GetMapping("/register")
	public String getRegister(Model model, @ModelAttribute RegisterForm form) {

		// ヘッダーの色と項目を設定する.
		customHeader.setRed("入荷先登録");
		model.addAttribute("customHeader", customHeader);

		return "/suppliers/register";
	}

	/** 入荷先登録フォーム画面の登録を押してくるところ. */
	@PostMapping("/register")
	public String postRegister(Model model, @ModelAttribute @Validated RegisterForm form,
			BindingResult bindingResult) {

		// バリデーションエラーがあれば入荷先登録フォーム画面へ戻る.
		if (bindingResult.hasErrors()) {

			// ヘッダーの色と項目を設定する.
			customHeader.setRed("入荷先登録");
			model.addAttribute("customHeader", customHeader);

			return "/suppliers/register";
		}

		// formクラスをエンティティクラスに変換する.
		MSupplier supplier = modelMapper.map(form, MSupplier.class);

		// 入荷先を登録する.
		supplierService.registerOne(supplier);

		return "redirect:/suppliers/list";
	}

	/** 入荷先情報修正ボタンを押してくるところ. */
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

	/** 入荷先情報修正フォーム画面の確定ボタンを押してくるところ. */
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

	/** 入荷先情報削除ボタンを押してくるところ */
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

	/** 入荷先情報削除フォーム画面の削除ボタンを押してくるところ. */
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

	/** 入荷先情報修正フォーム画面への遷移と入荷先情報修正フォーム画面で確定ボタンを押した後のバリデーションエラー時にフォームに戻るときの共通処理をまとめたメソッド.　*/
	private void goToEdit(Model model, MSupplier supplier) {

		// 取得した入荷先情報を入荷先情報修正フォーム画面に渡すためmodelに格納する(supplierIdを渡すため).
		model.addAttribute("supplier", supplier);

		// ヘッダーの色と項目を設定する.
		customHeader.setRed("入荷先修正");
		model.addAttribute("customHeader", customHeader);
	}

}
