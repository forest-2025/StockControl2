package com.example.controller;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import com.example.component.CustomUserDetails;
import com.example.domain.customers.model.MCustomer;
import com.example.domain.products.model.MProduct;
import com.example.domain.products.service.ProductCountService;
import com.example.dto.products.ProductWithSupplier;
import com.example.dto.products.TTransactionHistory;
import com.example.form.products.count.ArriveForm;
import com.example.form.products.count.ShipForm;
import com.example.form.products.count.StockEditForm;
import com.example.validation.GroupOrder;

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
	 * @param productId 入荷する商品のID.
	 * @param form 入荷フォーム.
	 * @return 	パスパラメータの商品IDがDBに存在しなければエラー画面のビュー名.
	 * 			正常に完了した場合,入荷フォーム画面のビュー名.
	 */
	@GetMapping("/{productId}/count/arrive")
	public String getArrive(Model model, @PathVariable Integer productId, @ModelAttribute ArriveForm form) {
		// @PathVariableの引数のname属性は省略している.

		// 商品IDから商品情報と入荷先情報を取得する(削除済みは除く).
		ProductWithSupplier productWithSupplier = productCountService.getOneProductWithSupplier(productId);

		// 取得した商品情報が存在するか確認する(存在しなければエラー画面へ).
		if (productWithSupplier == null) {
			return "error";
		}

		/* productWithSupplierをmodelに格納する処理・ヘッダーの設定をmodel格納する処理をまとめたメソッドを呼び出している.
		 * (下のほうでprivateメソッドとして設定している). */
		this.goToArrive(model, productWithSupplier);

		return "products/count/arrive";
	}

	/**
	 * 入荷フォーム画面の確定ボタンを押してくるところ.
	 * 入荷フォームの内容を確認して入荷処理を行う.
	 * 
	 * @param model ビューにデータを渡すためのモデル.
	 * @param productId 入荷する商品のID.
	 * @param customUserDetails ログイン中のユーザーの情報.
	 * @param form 入荷フォーム.
	 * @param bindingResult バリデーションエラー.
	 * @return 	パスパラメータの商品IDがDBに存在しなければエラー画面のビュー名.
	 * 			バリデーションエラーがあれば入荷フォーム画面のビュー名.
	 * 			正常に完了した場合,商品一覧画面のビュー名(リダイレクト).
	 */
	@PostMapping("/{productId}/count/arrive")
	public String postArrive(Model model, @PathVariable Integer productId,
			@AuthenticationPrincipal CustomUserDetails customUserDetails,
			@ModelAttribute @Validated(GroupOrder.class) ArriveForm form,
			BindingResult bindingResult) {
		// @PathVariableの引数のname属性は省略している.

		// 商品IDから商品情報と入荷先情報を取得する(削除済みは除く).
		ProductWithSupplier productWithSupplier = productCountService.getOneProductWithSupplier(productId);

		// 取得した商品情報が存在するか確認する(存在しなければエラー画面へ).
		if (productWithSupplier == null) {
			return "error";
		}

		// バリデーションエラーがあれば入荷フォーム画面へ戻る.
		if (bindingResult.hasErrors()) {
			/* productWithSupplierをmodelに格納する処理・ヘッダーの設定をmodel格納する処理をまとめたメソッドを呼び出している.
			 * (下のほうでprivateメソッドとして設定している). */
			this.goToArrive(model, productWithSupplier);

			return "products/count/arrive";
		}

		//form（入荷数・備考）をTTransactionHistory型に変換する.
		TTransactionHistory transactionHistory = new TTransactionHistory();
		transactionHistory = modelMapper.map(form, TTransactionHistory.class);

		// 商品IDとユーザーIDを設定する.
		transactionHistory.setProductId(productId);
		transactionHistory.setUserId(customUserDetails.getUserId());

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
	 * @param productId 出荷する商品のID.
	 * @param form 出荷フォーム.
	 * @return 	パスパラメータの商品IDがDBに存在しなければエラー画面のビュー名.
	 * 			正常に完了した場合,出荷フォーム画面のビュー名.
	 */
	@GetMapping("/{productId}/count/ship")
	public String getShip(Model model, @PathVariable Integer productId, @ModelAttribute ShipForm form) {
		// @PathVariableの引数のname属性は省略している.

		// 商品IDから商品情報を取得する(削除済みは除く,また入荷先名は必要なし).
		MProduct product = productCountService.getOneProduct(productId);

		// 取得した商品情報が存在するか確認する(存在しなければエラー画面へ).
		if (product == null) {
			return "error";
		}

		/* productをmodelに格納する処理・出荷先名を取得しmodelに格納する処理・ヘッダーの設定をmodel格納する処理をまとめたメソッドを呼び出している.
		 * (下のほうでprivateメソッドとして設定している). */
		this.goToShip(model, product);

		return "products/count/ship";
	}

	/**
	 * 出荷フォーム画面の確定ボタンを押してくるところ.
	 * 出荷フォームの内容を確認して出荷処理を行う.
	 * 
	 * @param model ビューにデータを渡すためのモデル.
	 * @param productId 出荷する商品のID.
	 * @param customUserDetails ログイン中のユーザーの情報.
	 * @param form 出荷フォーム.
	 * @param bindingResult バリデーションエラー.
	 * @return 	パスパラメータの商品IDがDBに存在しなければエラー画面のビュー名.
	 * 			バリデーションエラーがあれば出荷フォーム画面のビュー名.
	 * 			正常に完了した場合,商品一覧画面のビュー名(リダイレクト).
	 */
	@PostMapping("/{productId}/count/ship")
	public String postShip(Model model, @PathVariable Integer productId,
			@AuthenticationPrincipal CustomUserDetails customUserDetails,
			@ModelAttribute @Validated(GroupOrder.class) ShipForm form,
			BindingResult bindingResult) {
		// @PathVariableの引数のname属性は省略している.

		// 商品IDから商品情報を取得する(削除済みは除く).
		MProduct product = productCountService.getOneProduct(productId);

		// 取得した商品情報が存在するか確認する(存在しなければエラー画面へ).
		if (product == null) {
			return "error";
		}

		// バリデーションエラーがあれば出荷フォーム画面へ戻る.
		if (bindingResult.hasErrors()) {

			/* productをmodelに格納する処理・出荷先名を取得しmodelに格納する処理・ヘッダーの設定をmodel格納する処理をまとめたメソッドを呼び出している.
			 * (下のほうでprivateメソッドとして設定している). */
			this.goToShip(model, product);

			return "products/count/ship";
		}

		// form（出荷先ID・出荷数・備考）をTTransactionHistory型に変換する.
		TTransactionHistory transactionHistory = new TTransactionHistory();
		transactionHistory = modelMapper.map(form, TTransactionHistory.class);

		// ユーザーIDを設定する.
		transactionHistory.setUserId(customUserDetails.getUserId());

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
	 * @param productId 在庫修正する商品のID.
	 * @param form 在庫修正フォーム.
	 * @return 	パスパラメータの商品IDがDBに存在しなければエラー画面のビュー名.
	 * 			正常に完了した場合,在庫修正フォーム画面のビュー名.
	 */
	@GetMapping("/{productId}/count/edit")
	public String getEdit(Model model, @PathVariable Integer productId, @ModelAttribute StockEditForm form) {
		// @PathVariableの引数のname属性は省略している.

		// 商品IDから商品情報を取得する(削除済みは除く,また入荷先情報は必要ない → 在庫の増加理由をsupplierIdに情報が入っていれば入荷、nullなら増加修正と判断できるようにするため).
		MProduct product = productCountService.getOneProduct(productId);

		// 取得した商品情報が存在するか確認する(存在しなければエラー画面へ).
		if (product == null) {
			return "error";
		}

		/* productをmodelに格納する処理・ヘッダーの設定をmodel格納する処理をまとめたメソッドを呼び出している.
		 * (下のほうでprivateメソッドとして設定している). */
		this.goToEdit(model, product);

		return "products/count/edit";

	}

	/**
	 * 在庫修正フォーム画面の確定ボタンを押してくるところ.
	 * 在庫修正フォームの内容を確認して修正処理を行う.
	 * 
	 * @param model ビューにデータを渡すためのモデル.
	 * @param productId 在庫修正する商品のID.
	 * @param customUserDetails ログイン中のユーザーの情報.
	 * @param form 在庫修正フォーム.
	 * @param bindingResult バリデーションエラー.
	 * @return 	パスパラメータの商品IDがDBに存在しなければエラー画面のビュー名.
	 * 			バリデーションエラーがあれば在庫修正フォーム画面のビュー名.
	 * 			正常に完了した場合,在庫修正した商品の詳細画面のビュー名(リダイレクト).
	 */
	@PostMapping("/{productId}/count/edit")
	public String postEdit(Model model, @PathVariable Integer productId,
			@AuthenticationPrincipal CustomUserDetails customUserDetails,
			@ModelAttribute @Validated(GroupOrder.class) StockEditForm form,
			BindingResult bindingResult) {
		// @PathVariableの引数のname属性は省略している.

		// 商品IDから商品情報を取得する(削除済みは除く,また入荷先情報は必要ない → 在庫の増加理由をsupplierIdに情報が入っていれば入荷、nullなら増加修正と判断できるようにするため).
		MProduct product = productCountService.getOneProduct(productId);

		// 取得した商品情報が存在するか確認する(存在しなければエラー画面へ).
		if (product == null) {
			return "error";
		}

		// バリデーションエラーがあれば在庫修正フォーム画面へ戻る.
		if (bindingResult.hasErrors()) {
			/* productをmodelに格納する処理・ヘッダーの設定をmodel格納する処理をまとめたメソッドを呼び出している.
			 * (下のほうでprivateメソッドとして設定している). */
			this.goToEdit(model, product);

			return "products/count/edit";
		}

		// form（実在庫数・備考）をTTransactionHistory型に変換する.
		TTransactionHistory transactionHistory = new TTransactionHistory();
		transactionHistory = modelMapper.map(form, TTransactionHistory.class);

		// 商品の在庫数を取得する.
		Integer stockQuantity = productCountService.getOneStockQuantity(productId);

		/* 実在庫数と商品の在庫数との差をamountOfChange(在庫の増減数)に設定する.
		 * 実在庫数より商品の在庫数が少なければ在庫が増えたということなので正の数がamountOfChangeに格納され,
		 * 実在庫数より商品の在庫数が多ければ在庫が減ったということなので負の数が格納される. */
		Integer amountOfChange = form.getActualProductCount() - stockQuantity;

		// ユーザーID・在庫の増減数を設定する.
		transactionHistory.setUserId(customUserDetails.getUserId());
		transactionHistory.setAmountOfChange(amountOfChange);

		// 修正処理を行う(商品の在庫を調整して履歴を更新する).
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
	 */
	private void goToArrive(Model model, ProductWithSupplier productWithSupplier) {

		// 取得した商品情報のうちただ表示するためだけの情報(商品番号・商品名・入荷先)と遷移先として渡すための情報(商品ID)を入荷画面に渡すためmodelに格納する.
		model.addAttribute("productWithSupplier", productWithSupplier);

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
	 */
	private void goToShip(Model model, MProduct product) {

		// 商品情報をmodelに格納する.
		model.addAttribute("product", product);

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
	 */
	private void goToEdit(Model model, MProduct product) {

		// 商品情報をmodelに格納する.
		model.addAttribute("product", product);

		// ヘッダーの色と項目を設定する.
		customHeader.setGray("在庫修正");
		model.addAttribute("customHeader", customHeader);

	}

}
