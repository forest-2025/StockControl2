package com.example.controller;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.multipart.MultipartFile;

import com.example.component.CustomHeader;
import com.example.domain.products.model.MProduct;
import com.example.domain.products.service.ProductInfoService;
import com.example.domain.suppliers.model.MSupplier;
import com.example.dto.products.HistoryDetails;
import com.example.dto.products.ProductList;
import com.example.dto.products.ProductWithSupplier;
import com.example.dto.products.UploadResult;
import com.example.form.products.info.ImageEditForm;
import com.example.form.products.info.ProductEditForm;
import com.example.form.products.info.RegisterForm;
import com.example.validation.GroupOrder;
import com.github.pagehelper.PageInfo;

/**
 * 商品の情報に関するコントローラクラス.
 *
 */
@Controller
@RequestMapping("/products")
public class ProductInfoController {

	@Autowired
	private CustomHeader customHeader;

	@Autowired
	private ProductInfoService productInfoService;

	@Autowired
	private ModelMapper modelMapper;

	// プロパティファイルや環境変数の値を直接Javaのフィールドに注入する(application.properties で定義されたプロパティを参照).
	@Value("${file.upload-dir}")
	private String uploadDir;

	/**
	 * 商品一覧画面（ホーム画面）へ遷移する.
	 * 
	 * @param model ビューにデータを渡すためのモデル.
	 * @param search 商品を検索するときの検索語句.
	 * @param sort 商品一覧を並び替えるときの並び替え順序(昇順または降順).
	 * @param page 取得するページ番号.
	 * @return 商品一覧画面のビュー名.
	 */
	@GetMapping("/info/list")
	public String getList(Model model,
			@RequestParam(required = false) String search,
			@RequestParam(defaultValue = "asc") String sort,
			@RequestParam(defaultValue = "1") int page) {

		/*@RequestParamのrequired属性をfalseにすることで検索パラメータ（URLの末尾の？に続く変数）の,
		 * パラメータ名searchがあってもなくても受け付けられるようにしている.
		 * パラメータ名searchが無ければ削除されていない全商品の一覧を取得し,あればsearchの値が含まれる商品を検索する. */

		PageInfo<ProductList> productList = productInfoService.findAllSorted(search, sort, page);
		model.addAttribute("productList", productList);
		model.addAttribute("search", search);

		// ヘッダーの色と項目を設定する.
		customHeader.setGray("商品一覧");
		model.addAttribute("customHeader", customHeader);

		return "/products/info/list";
	}

	/**
	 * 商品登録ボタンを押してくるところ.
	 * 商品登録フォーム画面へ遷移する.
	 * 
	 * @param model ビューにデータを渡すためのモデル.
	 * @param form 商品登録フォーム.
	 * @return 商品登録フォーム画面のビュー名.
	 */
	@GetMapping("/info/register")
	public String getRegister(Model model, @ModelAttribute RegisterForm form) {

		/* 入荷先名全件取得しmodelに格納する処理,ヘッダーの設定をmodelに格納する処理をまとめたメソッドを呼び出している(下のほうでprivateメソッドとして設定している). */
		this.goToRegister(model);

		return "/products/info/register";
	}

	/**
	 * 商品登録フォーム画面で登録ボタンを押したときにくるところ.
	 * 商品情報の登録内容を確認して登録する.
	 * 
	 * @param model ビューにデータを渡すためのモデル.
	 * @param form 商品登録フォーム.
	 * @param bindingResult バリデーションエラー.
	 * @return 	バリデーションエラーがあれば商品登録フォーム画面のビュー名.
	 * 			正常に完了した場合,商品一覧画面のビュー名(リダイレクト).
	 */
	@PostMapping("/info/register")
	public String postRegister(Model model,
			@ModelAttribute @Validated(GroupOrder.class) RegisterForm form,
			BindingResult bindingResult) {

		/* MultipartFile型はSpringのアップロードされたファイルを扱うためのオブジェクト.
		 * ファイル名・サイズ・MIMEタイプ(ファイルの種類を表す情報でタイプ/サブタイトルの形式(image/jpegみたいな)をしている)・内容（バイト配列）などをもつ. */
		MultipartFile file = form.getProductFile();

		UploadResult result = new UploadResult();

		// 画像ファイルがあれば,画像ファイルのバリデーションチェックと画像の保存を行う(画像選択していなければnullではないがfile.isEmpty()がTrueになる).
		if (file != null && !file.isEmpty()) {
			result = productInfoService.validateAndUpload(file, result);
		}

		/* バリデーションエラーがあれば商品登録フォーム画面へ戻る.
		 * 画像がなければresultのfileName ・ errorsは両方ともnullでhasErrors()はfalseになる. */
		if (bindingResult.hasErrors() || result.hasErrors()) {
			model.addAttribute("errors", result.getErrors());
			// 入荷先名全件取得しmodelに格納する処理,ヘッダーの設定をmodelに格納する処理をまとめたメソッドを呼び出している(下のほうでprivateメソッドとして設定している).
			this.goToRegister(model);

			return "/products/info/register";
		}

		// formクラスをエンティティクラスに変換する.
		MProduct product = modelMapper.map(form, MProduct.class);

		// 画像ファイルを設定する(画像ファイルがなければnullがはいってる).
		product.setProductImage(result.getFileName());

		// 商品登録を行う.
		productInfoService.registerProduct(product);

		return "redirect:/products/info/list";

	}

	/**
	 * 詳細ボタンを押したときにくるところ.
	 * 詳細情報画面へ遷移する.
	 * 
	 * @param model ビューにデータを渡すためのモデル.
	 * @param productId 詳細を表示する商品のID.
	 * @param page 取得するページ番号.
	 * @return 	パスパラメータの商品IDがDBに存在しなければエラー画面のビュー名.
	 * 			正常に完了した場合,詳細情報画面のビュー名.
	 */
	@GetMapping("/{productId}/info/display-details")
	public String getDisplayDetails(Model model, @PathVariable Integer productId,
			@RequestParam(defaultValue = "1") int page) {

		// 商品IDから商品情報を取得する(削除済みは除く).
		ProductList oneItem = productInfoService.getOneItemInTheList(productId);

		// 取得した商品情報が存在するか確認する(存在しなければエラー画面へ).
		if (oneItem == null) {
			return "/error";
		}

		// modelに格納する.
		model.addAttribute("oneItem", oneItem);

		// 商品IDからその商品の履歴を降順で取得してmodelに格納する.
		PageInfo<HistoryDetails> historyList = productInfoService.getHistoryForOneProduct(page, productId);
		model.addAttribute("historyList", historyList);

		// ヘッダーの色と項目を設定する.
		customHeader.setGray("詳細情報");
		model.addAttribute("customHeader", customHeader);

		return "/products/info/display-details";
	}

	/**
	 * 商品情報修正ボタンを押してくるところ.
	 * 商品情報修正フォーム画面へ遷移する.
	 * 
	 * @param model ビューにデータを渡すためのモデル.
	 * @param productId 商品情報を修正する商品のID.
	 * @param form 商品情報修正フォーム.
	 * @return 	パスパラメータの商品IDがDBに存在しなければエラー画面のビュー名.
	 * 			正常に完了した場合,商品情報修正フォーム画面のビュー名.
	 */
	@GetMapping("/{productId}/info/edit")
	public String getEdit(Model model, @PathVariable Integer productId, @ModelAttribute ProductEditForm form) {

		// 商品IDから商品情報と入荷先情報を取得する(削除済みは除く.初期値として入荷先名も表示したいためMProductではなくProductWithSupplierを使用している).
		ProductWithSupplier productWithSupplier = productInfoService.getOneProductWithSupplier(productId);

		// 取得した商品情報が存在するか確認する(存在しなければエラー画面へ).
		if (productWithSupplier == null) {
			return "/error";
		}

		/* ModelMapperによる自動マッピングだとformクラスのフィールドproductIdがマッピングされず,エラーになるため一つずつ設定している.
		 * (productIdは@PathVariableのInteger productIdの値が自動で入っているため設定していない). */
		form.setProductName(productWithSupplier.getProduct().getProductName());
		form.setProductNumber(productWithSupplier.getProduct().getProductNumber());
		form.setSupplierId(productWithSupplier.getSupplier().getSupplierId());
		model.addAttribute("productEditForm", form);

		/* 商品IDと入荷先名フォームに初期値を渡すために商品情報と入荷先情報をmodelに格納する処理・入荷先名全件取得しmodelに格納する処理,
		 * ヘッダーの設定をmodelに格納する処理をまとめたメソッドを呼び出している(下のほうでprivateメソッドとして設定している). */
		this.goToEdit(model, productWithSupplier);

		return "/products/info/edit";
	}

	/**
	 * 商品情報修正フォーム画面の確定ボタンを押してくるところ.
	 * 商品情報の修正内容を確認して更新する.
	 * 
	 * @param model ビューにデータを渡すためのモデル.
	 * @param productId 商品情報を修正する商品のID.
	 * @param form 商品情報修正フォーム.
	 * @param bindingResult バリデーションエラー.
	 * @return 	パスパラメータの商品IDがDBに存在しなければエラー画面のビュー名.
	 * 			バリデーションエラーがあれば商品情報修正フォーム画面のビュー名.
	 * 			正常に完了した場合,商品情報を修正した商品の詳細画面のビュー名(リダイレクト).
	 */
	@PostMapping("/{productId}/info/edit")
	public String postEdit(Model model, @PathVariable Integer productId,
			@ModelAttribute @Validated(GroupOrder.class) ProductEditForm form,
			BindingResult bindingResult) {
		// @PathVariableの引数のname属性は省略している.

		/* 商品IDから商品情報を取得する.
		 * (削除済みは除く.また商品番号が変更されていない場合,商品番号の重複チェックを行う際に元の商品番号と重複していると誤判断することを防ぐために比較対象として取得する.
		 * また,MProductではなくProductWithSupplierで情報を取得するのは,バリデーションエラーがあったときに商品IDと入荷先名と入荷先IDを商品情報修正画面に渡したいから. */
		ProductWithSupplier productWithSupplier = productInfoService.getOneProductWithSupplier(productId);

		// 取得した商品情報が存在するか確認する(存在しなければエラー画面へ).
		if (productWithSupplier == null) {
			return "/error";
		}

		// バリデーションエラーがあれば商品情報修正フォーム画面へ戻る.
		if (bindingResult.hasErrors()) {

			/* 商品IDを渡すために商品情報と入荷先情報をmodelに格納する処理・入荷先名全件取得しmodelに格納する処理,
			 * ヘッダーの設定をmodel格納する処理をまとめたメソッドを呼び出している(下のほうでprivateメソッドとして設定している). */
			this.goToEdit(model, productWithSupplier);

			return "/products/info/edit";
		}

		// formクラスをエンティティクラスに変換する.
		MProduct product = modelMapper.map(form, MProduct.class);
		// 商品IDを設定する.
		product.setProductId(productId);

		// 商品情報を更新する.
		productInfoService.updateProduct(product);

		return "redirect:/products/" + productId + "/info/display-details";

	}

	/**
	 * 画像修正ボタンを押してくるところ.
	 * 画像修正フォーム画面へ遷移する.
	 * 
	 * @param model ビューにデータを渡すためのモデル.
	 * @param productId 画像を修正する商品のID.
	 * @param form 画像修正フォーム.
	 * @return 	パスパラメータの商品IDがDBに存在しなければエラー画面のビュー名.
	 * 			正常に完了した場合,画像修正フォーム画面のビュー名.
	 */
	@GetMapping("/{productId}/info/imageEdit")
	public String getImageEdit(Model model, @PathVariable Integer productId, @ModelAttribute ImageEditForm form) {

		// 商品IDから商品情報を取得する(削除済みは除く).
		MProduct product = productInfoService.getOneProduct(productId);

		// 取得した商品情報が存在するか確認する(存在しなければエラー画面へ).
		if (product == null) {
			return "/error";
		}

		// modelに格納する.
		model.addAttribute("product", product);
		model.addAttribute("imageEditForm", form);

		// ヘッダーの色と項目を設定する.
		customHeader.setGray("画像修正");
		model.addAttribute("customHeader", customHeader);

		return "/products/info/image-edit";
	}

	/**
	 * 画像修正フォームの確定ボタンを押してくるところ.
	 * 画像の修正内容を確認して更新する.
	 * 
	 * @param model ビューにデータを渡すためのモデル.
	 * @param productId 画像を修正する商品のID.
	 * @param form 画像修正フォーム.
	 * @param bindingResult バリデーションエラー.
	 * @return 	パスパラメータの商品IDがDBに存在しなければエラー画面のビュー名.
	 * 			バリデーションエラーがあれば画像修正フォーム画面のビュー名.
	 * 			正常に完了した場合,画像を修正した商品の詳細画面のビュー名(リダイレクト).
	 */
	@PostMapping("/{productId}/info/imageEdit")
	public String postImageEdit(Model model, @PathVariable Integer productId,
			@ModelAttribute @Validated(GroupOrder.class) ImageEditForm form, BindingResult bindingResult) {

		// 商品IDから商品情報を取得する(削除済みは除く).
		MProduct product = productInfoService.getOneProduct(productId);

		// 取得した商品情報が存在するか確認する(存在しなければエラー画面へ).
		if (product == null) {
			return "/error";
		}

		/* MultipartFile型はSpringのアップロードされたファイルを扱うためのオブジェクト.
		 * ファイル名・サイズ・MIMEタイプ(ファイルの種類を表す情報でタイプ/サブタイトルの形式(image/jpegみたいな)をしている)・内容（バイト配列）などをもつ. */
		MultipartFile file = form.getProductFile();

		UploadResult result = new UploadResult();

		// 画像ファイルがあれば,画像ファイルのバリデーションチェックと画像の保存を行う.
		if (file != null && !file.isEmpty()) {
			result = productInfoService.validateAndUpload(file, result);
		}

		// バリデーションエラーがあれば商品登録フォーム画面へ戻る.
		if (result.hasErrors()) {

			// modelに格納する.
			model.addAttribute("product", product);
			model.addAttribute("errors", result.getErrors());

			// ヘッダーの色と項目を設定する.
			customHeader.setGray("画像修正");
			model.addAttribute("customHeader", customHeader);

			return "/products/info/image-edit";
		}

		// 画像情報をオブジェクトに設定する.
		MProduct productImageEdit = new MProduct();
		productImageEdit.setProductId(productId);
		productImageEdit.setProductImage(result.getFileName());

		// 画像情報を更新する.
		productInfoService.updateProductImage(product, productImageEdit);

		return "redirect:/products/" + productId + "/info/display-details";

	}

	/**
	 * 商品情報削除ボタンを押してくるところ.
	 * 商品情報削除フォーム画面へ遷移する.
	 * 
	 * @param model ビューにデータを渡すためのモデル.
	 * @param productId 商品情報を削除する商品のID.
	 * @return 	パスパラメータの商品IDがDBに存在しなければエラー画面のビュー名.
	 * 			正常に完了した場合,商品情報削除フォーム画面のビュー名.
	 */
	@GetMapping("/{productId}/info/delete")
	public String getDelete(Model model, @PathVariable Integer productId) {

		// 商品IDから商品情報と入荷先情報を取得する(削除済みは除く).
		ProductWithSupplier productWithSupplier = productInfoService.getOneProductWithSupplier(productId);

		// 取得した商品情報が存在するか確認する(存在しなければエラー画面へ).
		if (productWithSupplier == null) {
			return "/error";
		}

		// 取得した商品情報を商品情報削除フォーム画面に渡すためmodelに格納する(値を表示するだけで変更しないのでそのまま渡す).
		model.addAttribute("productWithSupplier", productWithSupplier);

		// ヘッダーの色と項目を設定する.
		customHeader.setGray("商品削除");
		model.addAttribute("customHeader", customHeader);

		return "/products/info/delete";
	}

	/**
	 * 商品情報削除フォーム画面の削除ボタンを押してくるところ.
	 * 商品情報の削除フラグを更新(1にして論理削除)する.
	 * 
	 * @param model ビューにデータを渡すためのモデル.
	 * @param productId 商品情報を削除する商品のID.
	 * @return 	パスパラメータの商品IDがDBに存在しなければエラー画面のビュー名.
	 * 			正常に完了した場合,商品一覧画面のビュー名(リダイレクト).
	 */
	@PostMapping("/{productId}/info/delete")
	public String postDelete(Model model, @PathVariable Integer productId) {

		/// 商品IDから商品情報を取得する(削除済みは除く).
		MProduct productOne = productInfoService.getOneProduct(productId);

		// 取得した商品情報が存在するか確認する(存在しなければエラー画面へ).
		if (productOne == null) {
			return "/error";
		}

		// 商品情報(削除フラグ)を更新する.
		productInfoService.updateIsDeleted(productOne);

		return "redirect:/products/info/list";
	}

	// ==========================================
	//    privateメソッド.
	// ==========================================

	/**
	 *  商品登録フォーム画面への遷移と,
	 *  商品登録フォーム画面で登録ボタンを押した後のバリデーションエラー時にフォーム画面に戻るときの共通処理をまとめたメソッド.
	 * 
	 * @param model ビューにデータを渡すためのモデル.
	 */
	private void goToRegister(Model model) {

		// 削除済み以外の入荷先名を取得し、modelに格納して画面に渡す.
		List<MSupplier> supplierList = productInfoService.getAllSupplier();
		model.addAttribute("supplierList", supplierList);

		// ヘッダーの色と項目を設定する.
		customHeader.setGray("商品登録");
		model.addAttribute("customHeader", customHeader);
	}

	/**
	 * 商品情報修正フォーム画面への遷移と,
	 * 商品情報修正フォーム画面で確定ボタンを押した後のバリデーションエラー時にフォームに戻るときの共通処理をまとめたメソッド.
	 * 
	 * @param model ビューにデータを渡すためのモデル.
	 * @param productWithSupplier 商品情報を修正する商品の情報と入荷先の情報.
	 */
	private void goToEdit(Model model, ProductWithSupplier productWithSupplier) {

		// 取得した商品情報と入荷先情報をmodelに格納する.
		model.addAttribute("productWithSupplier", productWithSupplier);

		// 削除済み以外の入荷先名を全件取得し,modelに格納して画面に渡す.
		List<MSupplier> supplierList = productInfoService.getAllSupplier();
		model.addAttribute("supplierList", supplierList);

		// ヘッダーの色と項目を設定する.
		customHeader.setGray("商品修正");
		model.addAttribute("customHeader", customHeader);
	}

}
