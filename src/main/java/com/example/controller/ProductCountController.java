package com.example.controller;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.component.CustomHeader;
import com.example.component.FullNameUser;
import com.example.domain.customers.model.MCustomer;
import com.example.domain.products.model.MProduct;
import com.example.domain.products.model.ProductWithSupplier;
import com.example.domain.products.service.ProductCountService;
import com.example.dto.products.TTransactionHistory;
import com.example.form.products.count.ArriveForm;
import com.example.form.products.count.ShipForm;
import com.example.form.products.count.StockEditForm;

/**
 * 商品の数量の増減に関するコントローラクラス.
 *
 */
@Controller
@RequestMapping("/products")
public class ProductCountController {

	@Autowired
	private CustomHeader customHeader;

	@Autowired
	private ProductCountService productCountService;

	@Autowired
	private ModelMapper modelMapper;

	// ==========================================
	//    入荷の処理.
	// ==========================================

	/**
	 * 入荷ボタンを押してくるところ.
	 * 入荷フォーム画面へ遷移する.
	 * 
	 * @param model ビューにデータを渡すためのモデル.
	 * @param productId　入荷する商品のID.
	 * @param fullNameUser　ログイン中のユーザーのフルネーム(姓 + 名).
	 * @param form 入荷フォーム.
	 * @return 入荷フォーム画面のビュー名.
	 */
	@GetMapping("/{productId}/count/arrive")
	public String getArrive(Model model, @PathVariable Integer productId,
			@AuthenticationPrincipal FullNameUser fullNameUser, @ModelAttribute ArriveForm form) {
		// @PathVariableの引数のname属性は省略している.

		// 商品IDから商品情報と入荷先情報を取得する(削除済みは除く).
		ProductWithSupplier productWithSupplier = productCountService.getOneProductWithSupplier(productId);

		// 取得した商品情報が存在するか確認する(存在しなければエラー画面へ).
		if (productWithSupplier == null) {
			return "/error";
		}

		/* productWithSupplierをmodelに格納する処理・ログイン中のユーザーのフルネームを取得しmodelに格納する処理・
		ヘッダーの設定をmodel格納する処理をまとめたメソッドを呼び出している(下のほうでprivateメソッドとして設定している). */
		this.goToArrive(model, productWithSupplier, fullNameUser);

		return "/products/count/arrive";
	}

	/**
	 * 入荷フォーム画面の確定ボタンを押してくるところ.
	 * 入荷フォームの内容を確認して入荷処理を行う.
	 * 
	 * @param model ビューにデータを渡すためのモデル.
	 * @param productId　入荷する商品のID.
	 * @param fullNameUser　ログイン中のユーザーのフルネーム(姓 + 名).
	 * @param userDetails ログイン中のユーザーの情報.
	 * @param form 入荷フォーム.
	 * @param　bindingResult　バリデーションエラー.
	 * @return バリデーションエラーがあれば入荷フォーム画面のビュー名,なければ商品一覧画面のビュー名(こちらならリダイレクト).
	 */
	@PostMapping("/{productId}/count/arrive")
	public String postArrive(Model model, @PathVariable Integer productId,
			@AuthenticationPrincipal FullNameUser fullNameUser,
			@AuthenticationPrincipal UserDetails userDetails,
			@ModelAttribute @Validated ArriveForm form,
			BindingResult bindingResult) {
		// @PathVariableの引数のname属性は省略している.

		// 商品IDから商品情報と入荷先情報を取得する(削除済みは除く).
		ProductWithSupplier productWithSupplier = productCountService.getOneProductWithSupplier(productId);

		// 取得した商品情報が存在するか確認する(存在しなければエラー画面へ).
		if (productWithSupplier == null) {
			return "/error";
		}

		// バリデーションエラーがあれば入荷フォーム画面へ戻る.
		if (bindingResult.hasErrors()) {
			/* productWithSupplierをmodelに格納する処理・ログイン中のユーザーのフルネームを取得しmodelに格納する処理・
			ヘッダーの設定をmodel格納する処理をまとめたメソッドを呼び出している(下のほうでprivateメソッドとして設定している). */
			this.goToArrive(model, productWithSupplier, fullNameUser);

			return "/products/count/arrive";
		}

		//form（商品ID・入荷数・日付・備考）をTTransactionHistory型に変換する.
		TTransactionHistory transactionHistory = new TTransactionHistory();
		transactionHistory = modelMapper.map(form, TTransactionHistory.class);

		// 入荷履歴を更新するため、担当者（ログイン中のユーザーの姓と名）からm_userテーブルのIDを取得する(削除済みは除く).
		Integer userId = productCountService.getUserId(userDetails);

		// 商品IDとユーザーIDを設定する.
		transactionHistory.setProductId(productId);
		transactionHistory.setUserId(userId);

		// ProductWithSupplierクラスのMSupplier型フィールドに格納されているインスタンスのsupplierIdフィールドの値を取得する.
		Integer supplierId = productWithSupplier.getSupplier().getSupplierId();

		// 入荷IDを設定する.
		transactionHistory.setSupplierId(supplierId);

		// 入荷処理を行う（在庫を増やして入荷履歴を更新する）..
		productCountService.processArrival(transactionHistory);

		return "redirect:/products/info/list";
	}

	// ==========================================
	//    出荷の処理.
	// ==========================================

	/**
	 * 出荷ボタンを押してくるところ.
	 * 出荷フォーム画面へ遷移する.
	 * 
	 * @param model ビューにデータを渡すためのモデル.
	 * @param productId　出荷する商品のID.
	 * @param fullNameUser　ログイン中のユーザーのフルネーム(姓 + 名).
	 * @param form 出荷フォーム.
	 * @return 出荷フォーム画面のビュー名.
	 */
	@GetMapping("/{productId}/count/ship")
	public String getShip(Model model, @PathVariable Integer productId,
			@AuthenticationPrincipal FullNameUser fullNameUser, @ModelAttribute ShipForm form) {
		// @PathVariableの引数のname属性は省略している.

		// 商品IDから商品情報を取得する(削除済みは除く,また入荷先名は必要なし).
		MProduct product = productCountService.getOneProduct(productId);

		// 取得した商品情報が存在するか確認する(存在しなければエラー画面へ).
		if (product == null) {
			return "/error";
		}

		/* productをmodelに格納する処理・ログイン中のユーザーのフルネームを取得しmodelに格納する処理・出荷先名を取得しmodelに格納する処理・
		ヘッダーの設定をmodel格納する処理をまとめたメソッドを呼び出している(下のほうでprivateメソッドとして設定している). */
		this.goToShip(model, product, fullNameUser);

		return "/products/count/ship";
	}

	/**
	 * 出荷フォーム画面の確定ボタンを押してくるところ.
	 * 出荷フォームの内容を確認して出荷処理を行う.
	 * 
	 * @param model ビューにデータを渡すためのモデル.
	 * @param productId　出荷する商品のID.
	 * @param fullNameUser　ログイン中のユーザーのフルネーム(姓 + 名).
	 * @param userDetails ログイン中のユーザーの情報.
	 * @param form 出荷フォーム.
	 * @param　bindingResult　バリデーションエラー.
	 * @return バリデーションエラーがあれば出荷フォーム画面のビュー名,なければ商品一覧画面のビュー名(こちらならリダイレクト).
	 */
	@PostMapping("/{productId}/count/ship")
	public String postShip(Model model, @PathVariable Integer productId,
			@AuthenticationPrincipal FullNameUser fullNameUser,
			@AuthenticationPrincipal UserDetails userDetails,
			@ModelAttribute @Validated ShipForm form,
			BindingResult bindingResult) {
		// @PathVariableの引数のname属性は省略している.

		// 商品IDから商品情報を取得する(削除済みは除く).
		MProduct product = productCountService.getOneProduct(productId);

		// 取得した商品情報が存在するか確認する(存在しなければエラー画面へ).
		if (product == null) {
			return "/error";
		}

		// 出荷先IDを取得し、出荷先IDから出荷先情報を取得する(削除済み以外).
		Integer customerId = form.getCustomerId();
		MCustomer customer = productCountService.getCustomer(customerId);
		/* 出荷先IDがnullでなくて(出荷先がフォームで選択されていて)、そのIDから取得した出荷先情報がnullでない
		 * (出荷先情報に登録がある)ことを確認する.*/
		if (customerId == null) {
			// null(出荷先が選択されていない)ならif文を抜けて@NotNullのエラーメッセージが表示されるのでなにもしない.
		} else if (customer == null) {
			// customerIdで検索した出荷先が出荷先情報に登録されていないとき,または削除済みの場合はエラーとエラーメッセージを追加する.
			bindingResult.rejectValue("customerId", "NotCustomer");
		}

		// 出荷数と在庫数を取得する.
		Integer amountOfChange = form.getAmountOfChange();
		Integer stockQuantity = productCountService.getOneStockQuantity(productId);

		// 出荷数がnullではなくて、また在庫数を超えていないか確認する.
		if (amountOfChange == null) {
			// nullならif文を抜けて@NotNullのエラーメッセージが表示されるのでなにもしない.
		} else if (stockQuantity < amountOfChange) {
			// 出荷数が在庫数を超えていたらエラーとエラーメッセージを追加する.
			bindingResult.rejectValue("amountOfChange", "QuantityExceedsStock");
		}

		// バリデーションエラーがあれば出荷フォーム画面へ戻る.
		if (bindingResult.hasErrors()) {

			/* productをmodelに格納する処理・ログイン中のユーザーのフルネームを取得しmodelに格納する処理・出荷先名を取得しmodelに格納する処理・
			ヘッダーの設定をmodel格納する処理をまとめたメソッドを呼び出している(下のほうでprivateメソッドとして設定している). */
			this.goToShip(model, product, fullNameUser);

			return "/products/count/ship";
		}

		// form（出荷先ID・出荷数・備考）をTTransactionHistory型に変換する.
		TTransactionHistory transactionHistory = new TTransactionHistory();
		transactionHistory = modelMapper.map(form, TTransactionHistory.class);

		// 出荷履歴を更新するため、担当者（ログイン中のユーザーの姓と名）からm_userテーブルのIDを取得する(削除済みは除く).
		Integer userId = productCountService.getUserId(userDetails);

		// 商品IDとユーザーIDを設定する.
		transactionHistory.setProductId(productId);
		transactionHistory.setUserId(userId);

		// 出荷処理を行う.(商品の在庫を減少させて履歴を更新する).
		productCountService.processShip(transactionHistory);

		return "redirect:/products/info/list";
	}

	// ==========================================
	//    在庫数の修正処理.
	// ==========================================

	/**
	 * 詳細情報画面の在庫修正ボタンを押してくるところ.
	 * 在庫修正フォーム画面へ遷移する.
	 * 
	 * @param model ビューにデータを渡すためのモデル.
	 * @param productId　在庫修正する商品のID.
	 * @param fullNameUser　ログイン中のユーザーのフルネーム(姓 + 名).
	 * @param form 在庫修正フォーム.
	 * @return 在庫修正フォーム画面のビュー名.
	 */
	@GetMapping("/{productId}/count/edit")
	public String getEdit(Model model, @PathVariable Integer productId,
			@AuthenticationPrincipal FullNameUser fullNameUser, @ModelAttribute StockEditForm form) {
		// @PathVariableの引数のname属性は省略している.

		// 商品IDから商品情報を取得する(削除済みは除く,また入荷先情報は必要ない → 在庫の増加理由をsupplierIdに情報が入っていれば入荷、nullなら増加修正と判断できるようにするため).
		MProduct product = productCountService.getOneProduct(productId);

		// 取得した商品情報が存在するか確認する(存在しなければエラー画面へ).
		if (product == null) {
			return "/error";
		}

		/* productをmodelに格納する処理・ログイン中のユーザーのフルネームを取得しmodelに格納する処理・
		ヘッダーの設定をmodel格納する処理をまとめたメソッドを呼び出している(下のほうでprivateメソッドとして設定している). */
		this.goToEdit(model, product, fullNameUser);

		return "/products/count/edit";

	}

	/**
	 * 在庫修正フォーム画面の確定ボタンを押してくるところ.
	 * 在庫修正フォームの内容を確認して修正処理を行う.
	 * 
	 * @param model ビューにデータを渡すためのモデル.
	 * @param productId　在庫修正する商品のID.
	 * @param fullNameUser　ログイン中のユーザーのフルネーム(姓 + 名).
	 * @param userDetails ログイン中のユーザーの情報.
	 * @param form 在庫修正フォーム.
	 * @param　bindingResult　バリデーションエラー.
	 * @return バリデーションエラーがあれば在庫修正フォーム画面のビュー名,なければ在庫修正した商品の詳細画面のビュー名(こちらならリダイレクト).
	 */
	@PostMapping("/{productId}/count/edit")
	public String postEdit(Model model, @PathVariable Integer productId,
			@AuthenticationPrincipal FullNameUser fullNameUser,
			@AuthenticationPrincipal UserDetails userDetails,
			@ModelAttribute @Validated StockEditForm form,
			BindingResult bindingResult) {
		// @PathVariableの引数のname属性は省略している.

		// 商品IDから商品情報を取得する(削除済みは除く,また入荷先情報は必要ない → 在庫の増加理由をsupplierIdに情報が入っていれば入荷、nullなら増加修正と判断できるようにするため).
		MProduct product = productCountService.getOneProduct(productId);

		// 取得した商品情報が存在するか確認する(存在しなければエラー画面へ).
		if (product == null) {
			return "/error";
		}

		// 在庫数と実在庫数が一緒だと在庫数の変動がないため処理の意味がないのでエラーにする.
		// 商品の在庫数を取得する.
		Integer stockQuantity = productCountService.getOneStockQuantity(productId);
		// 入力された実在庫数を取得する.
		Integer actualProductCount = form.getActualProductCount();
		if (stockQuantity == actualProductCount) {
			bindingResult.rejectValue("actualProductCount", "SameQuantity");
		}

		// バリデーションエラーがあれば在庫修正フォーム画面へ戻る.
		if (bindingResult.hasErrors()) {
			/* productをmodelに格納する処理・ログイン中のユーザーのフルネームを取得しmodelに格納する処理・
			ヘッダーの設定をmodel格納する処理をまとめたメソッドを呼び出している(下のほうでprivateメソッドとして設定している). */
			this.goToEdit(model, product, fullNameUser);

			return "/products/count/edit";
		}

		// form（実在庫数・備考）をTTransactionHistory型に変換する.
		TTransactionHistory transactionHistory = new TTransactionHistory();
		transactionHistory = modelMapper.map(form, TTransactionHistory.class);

		// 修正履歴を更新するため、担当者（ログイン中のユーザーの姓と名）からm_userテーブルのIDを取得する(削除済みは除く).
		Integer userId = productCountService.getUserId(userDetails);

		/* 実在庫数と商品の在庫数との差をamountOfChange(在庫の増減数)に設定する.
		 * 実在庫数より商品の在庫数が少なければ在庫が増えたということなので正の数がamountOfChangeに格納され,
		 * 実在庫数より商品の在庫数が多ければ在庫が減ったということなので負の数が格納される. */
		Integer amountOfChange = actualProductCount - stockQuantity;

		// 商品ID・ユーザーID・在庫の増減数をを設定する.
		transactionHistory.setProductId(productId);
		transactionHistory.setUserId(userId);
		transactionHistory.setAmountOfChange(amountOfChange);

		// 修正処理を行う.(商品の在庫を調整して履歴を更新する).
		productCountService.processEdit(transactionHistory);

		return "redirect:/products/" + productId + "/info/display-details";
	}

	// ==========================================
	//    privateメソッド.
	// ==========================================

	/**
	 * 入荷フォーム画面への遷移と,
	 * 入荷フォーム画面で確定ボタンを押した後のバリデーションエラー時にフォームに戻るときの共通処理をまとめたメソッド.
	 * 
	 * @param model ビューにデータを渡すためのモデル.
	 * @param productWithSupplier 入荷する商品の情報.
	 * @param　fullNameUser ログイン中のユーザーのフルネーム(姓 + 名).
	 */
	private void goToArrive(Model model, ProductWithSupplier productWithSupplier,
			FullNameUser fullNameUser) {

		// 取得した商品情報のうちただ表示するためだけの情報(商品番号・商品名・入荷先)と遷移先として渡すための情報(商品ID)を入荷画面に渡すためmodelに格納する.
		model.addAttribute("productWithSupplier", productWithSupplier);

		// 担当者（ログイン中のユーザー）を入荷画面に渡すため、ログイン情報からFamilyNameとFirstNameを取得しmodelに格納する.
		String fullName = fullNameUser.getFamilyName() + fullNameUser.getFirstName();
		model.addAttribute("fullName", fullName);

		// ヘッダーの色と項目を設定する.
		customHeader.setRed("入荷");
		model.addAttribute("customHeader", customHeader);

	}

	/**
	 * 出荷フォーム画面への遷移と,
	 * 出荷フォーム画面で確定ボタンを押した後のバリデーションエラー時にフォームに戻るときの共通処理をまとめたメソッド.
	 * 
	 * @param model ビューにデータを渡すためのモデル.
	 * @param product 出荷する商品の情報.
	 * @param　fullNameUser ログイン中のユーザーのフルネーム(姓 + 名).
	 */
	private void goToShip(Model model, MProduct product, FullNameUser fullNameUser) {

		// 商品情報をmodelに格納する.
		model.addAttribute("product", product);

		// 担当者（ログイン中のユーザー）を入荷画面に渡すため、ログイン情報からFamilyNameとFirstNameを取得しmodelに格納する.
		String fullName = fullNameUser.getFamilyName() + fullNameUser.getFirstName();
		model.addAttribute("fullName", fullName);

		// 出荷画面に渡すため出荷先名を取得しmodelに格納する.
		List<MCustomer> customerList = productCountService.getCustomerList();
		model.addAttribute("customerList", customerList);

		// ヘッダーの色と項目を設定する.
		customHeader.setBlue("出荷");
		model.addAttribute("customHeader", customHeader);

	}

	/**
	 * 在庫修正フォーム画面への遷移と,
	 * 在庫修正フォーム画面で確定ボタンを押した後のバリデーションエラー時にフォームに戻るときの共通処理をまとめたメソッド.
	 * 
	 * @param model ビューにデータを渡すためのモデル.
	 * @param product 在庫修正する商品の情報.
	 * @param　fullNameUser ログイン中のユーザーのフルネーム(姓 + 名).
	 */
	/** 在庫修正フォーム画面への遷移と在庫修正フォーム画面で確定ボタンを押した後のバリデーションエラー時にフォームに戻るときの共通処理をまとめたメソッド　*/
	private void goToEdit(Model model, MProduct product, FullNameUser fullNameUser) {

		// 商品情報をmodelに格納する.
		model.addAttribute("product", product);

		// 担当者（ログイン中のユーザー）を入荷画面に渡すため、ログイン情報からFamilyNameとFirstNameを取得しmodelに格納する.
		String fullName = fullNameUser.getFamilyName() + fullNameUser.getFirstName();
		model.addAttribute("fullName", fullName);

		// ヘッダーの色と項目を設定する.
		customHeader.setGray("在庫修正");
		model.addAttribute("customHeader", customHeader);

	}

}
