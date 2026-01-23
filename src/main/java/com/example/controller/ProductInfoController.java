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
import com.example.domain.products.model.ProductList;
import com.example.domain.products.model.ProductWithSupplier;
import com.example.domain.products.service.ProductInfoService;
import com.example.domain.suppliers.model.MSupplier;
import com.example.dto.products.HistoryDetails;
import com.example.dto.products.UploadResult;
import com.example.form.products.info.ImageEditForm;
import com.example.form.products.info.ProductEditForm;
import com.example.form.products.info.RegisterForm;
import com.github.pagehelper.PageInfo;

@Controller
@RequestMapping("/products")
public class ProductInfoController {

	@Autowired
	private CustomHeader customHeader;

	@Autowired
	private ProductInfoService productInfoService;

	@Autowired
	private ModelMapper modelMapper;

	@Value("${file.upload-dir}")
	private String uploadDir;

	// 1ページで表示する商品数・入出荷履歴数を10に設定する.
	private static final int SHOW_SIZE = 10;

	/** 商品一覧画面（ホーム画面）へ遷移する. */
	@GetMapping("/info/list")
	public String getList(@RequestParam(required = false) String search,
			@RequestParam(defaultValue = "1") int page,
			Model model){

		/*@RequestParamのrequired属性をfalseにすることで検索パラメータ（URLの末尾の？に続く変数）の,
		 * パラメータ名searchがあってもなくても受け付けられるようにしている.
		 * パラメータ名searchが無ければ削除されていない全商品の一覧を取得し,あればsearchの値が含まれる商品を検索する.*/

		if (search == null) {
			PageInfo<ProductList> productList = productInfoService.getProductList(page, SHOW_SIZE);
			model.addAttribute("productList", productList);
		} else {
			PageInfo<ProductList> productList = productInfoService.getSearchProductList(page, SHOW_SIZE, search);
			model.addAttribute("productList", productList);
			model.addAttribute("search", search);
		}

		// ヘッダーの色と項目を設定する.
		customHeader.setGray("商品一覧");
		model.addAttribute("customHeader", customHeader);

		return "/products/info/list";
	}

	/** 商品登録フォーム画面へ遷移する. */
	@GetMapping("/info/register")
	public String getRegister(Model model, @ModelAttribute RegisterForm form) {

		/* 入荷先名全件取得しmodelに格納する処理,ヘッダーの設定をmodelに格納する処理をまとめたメソッドを呼び出している(下のほうでprivateメソッドとして設定している). */
		this.goToRegister(model, form);

		return "/products/info/register";
	}

	/** 商品登録フォーム画面で登録ボタンを押したときにくるところ. */
	@PostMapping("/info/register")
	public String postRegister(Model model, @ModelAttribute @Validated RegisterForm form,
			BindingResult bindingResult) {

		// 商品番号がnullや空白でないか確認する.
		String productNumber = form.getProductNumber();
		if (productNumber == null || productNumber.equals("")) {
			// nullまたは空白ならif文を抜けて@NotBlankのエラーメッセージが表示されるのでなにもしない.
		} else {
			boolean isNotDuplicate = productInfoService.isNotDuplicateProductNumber(productNumber);
			// 登録済みの商品番号と重複しないか確認する. 
			if (isNotDuplicate) {
				// trueなら商品番号に重複がないのでなにもしない.
			} else {
				// falseなら商品番号に重複があるのでエラーとエラーメッセージを追加する.
				bindingResult.rejectValue("productNumber", "DuplicateProductNumber");
			}
		}

		// 入荷先IDが入荷先情報に登録されているか確認する.
		Integer supplierId = form.getSupplierId();
		if (supplierId == null) {
			// nullならif文を抜けて@NotNullのエラーメッセージが表示されるのでなにもしない.
		} else {
			if (productInfoService.isRegister(supplierId)) {
				// trueなら入荷先が入荷先情報に登録されているのでなにもしない.
			} else {
				// falseなら入荷先が入荷先情報に登録されていない,または削除済みなのでエラーとエラーメッセージを追加する.
				bindingResult.rejectValue("supplierId", "NotSupplier");
			}

		}

		/* MultipartFile型はSpringのアップロードされたファイルを扱うためのオブジェクト.
		 * ファイル名・サイズ・MIMEタイプ(ファイルの種類を表す情報でタイプ/サブタイトルの形式(image/jpegみたいな)をしている)・内容（バイト配列）などをもつ. */
		MultipartFile file = form.getProductFile();

		UploadResult result = new UploadResult();
		
		// 画像ファイルがあれば,画像ファイルのバリデーションチェックと画像の保存を行う(画像選択していなければnullではないがfile.isEmpty()がTrueになる).
		if (file != null && !file.isEmpty()) {
			result = productInfoService.validateAndUpload(file, result);
		}

		// バリデーションエラーがあれば商品登録フォーム画面へ戻る.
		if (bindingResult.hasErrors() || result.hasErrors()) {
			model.addAttribute("errors", result.getErrors());
			/* 入荷先名全件取得しmodelに格納する処理,ヘッダーの設定をmodelに格納する処理をまとめたメソッドを呼び出している(下のほうでprivateメソッドとして設定している). */
			this.goToRegister(model, form);

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

	/** 詳細ボタンを押してくるところ. */
	@GetMapping("/{productId}/info/display-details")
	public String getDisplayDetails(@RequestParam(defaultValue = "1") int page,
			Model model, @PathVariable Integer productId) {

		// 商品IDから商品情報を取得する(削除済みは除く).
		ProductList oneItem = productInfoService.getOneItemInTheList(productId);

		// 取得した商品情報が存在するか確認する(存在しなければエラー画面へ).
		if (oneItem == null) {
			return "/error";
		}

		// modelに格納する.
		model.addAttribute("oneItem", oneItem);

		// 商品IDからその商品の履歴を降順で取得してmodelに格納する.
		PageInfo<HistoryDetails> historyList = productInfoService.getHistoryForOneProduct(page, SHOW_SIZE, productId);
		model.addAttribute("historyList", historyList);

		// ヘッダーの色と項目を設定する.
		customHeader.setGray("詳細情報");
		model.addAttribute("customHeader", customHeader);

		return "/products/info/display-details";
	}

	/** 商品情報修正ボタンを押してくるところ */
	@GetMapping("/{productId}/info/edit")
	public String getEdit(Model model, @PathVariable Integer productId, @ModelAttribute ProductEditForm form) {

		// 商品IDから商品情報と入荷先情報を取得する(削除済みは除く.初期値として入荷先名も表示したいためMProductではなくProductWithSupplierを使用している).
		ProductWithSupplier productWithSupplier = productInfoService.getOneProductWithSupplier(productId);

		// 取得した商品情報が存在するか確認する(存在しなければエラー画面へ).
		if (productWithSupplier == null) {
			return "/error";
		}

		// 取得した商品情報を商品情報修正画面に渡すためformに変換してmodelに格納する.
		form = modelMapper.map(productWithSupplier, ProductEditForm.class);
		model.addAttribute("productEditForm", form);

		/* 商品IDと入荷先名フォームに初期値を渡すために商品情報と入荷先情報をmodelに格納する処理・入荷先名全件取得しmodelに格納する処理,
		 * ヘッダーの設定をmodelに格納する処理をまとめたメソッドを呼び出している(下のほうでprivateメソッドとして設定している). */
		this.goToEdit(model, productWithSupplier);

		return "/products/info/edit";
	}

	/** 商品情報修正フォーム画面の確定ボタンを押してくるところ. */
	@PostMapping("/{productId}/info/edit")
	public String postEdit(Model model, @PathVariable Integer productId,
			@ModelAttribute @Validated ProductEditForm form,
			BindingResult bindingResult) {
		// @PathVariableの引数のname属性は省略している.

		/* 商品IDから商品情報を取得する.
		 * (削除済みは除く.また商品番号が変更されていない場合,商品番号の重複チェックを行う際に元の商品番号と重複していると誤判断することを防ぐために比較対象として取得する.
		 * また,MProductではなくProductWithSupplierで情報を取得するのはバリデーションエラーがあったときにproductIdを商品情報修正画面に渡したいが,
		 * 商品情報修正画面でProductWithSupplierで渡されるように設定してあるため(158行目もみる). */
		ProductWithSupplier productWithSupplier = productInfoService.getOneProductWithSupplier(productId);

		// 取得した商品情報が存在するか確認する(存在しなければエラー画面へ).
		if (productWithSupplier == null) {
			return "/error";
		}

		// 商品番号がnullや空白でないか確認する.
		String productNumber = form.getProductNumber();
		if (productNumber == null || productNumber.equals("")) {
			// nullまたは空白ならif文を抜けて@NotBlankのエラーメッセージが表示されるのでなにもしない.
		} else {
			boolean isNotDuplicate = productInfoService.isNotDuplicateProductNumber(productNumber);
			// 登録済みの商品番号と重複しないか確認する. 
			if (isNotDuplicate) {
				// trueなら商品番号に重複がないのでなにもしない.
			} else if (productNumber.equals(productWithSupplier.getProduct().getProductNumber())) {
				// 重複があっても,もとの商品番号と一緒ならtrueで商品番号に変更がなかっただけなのでなにもしない.
			} else {
				// falseなら商品番号に重複があるのでエラーとエラーメッセージを追加する.
				bindingResult.rejectValue("productNumber", "DuplicateProductNumber");
			}
		}

		// 入荷先IDが入荷先情報に登録されているか確認する.
		Integer supplierId = form.getSupplierId();
		if (supplierId == null) {
			// nullならif文を抜けて@NotNullのエラーメッセージが表示されるのでなにもしない.
		} else {
			if (productInfoService.isRegister(supplierId)) {
				// trueなら入荷先が入荷先情報に登録されているのでなにもしない.
			} else {
				// falseなら入荷先が入荷先情報に登録されていない,または削除済みなのでエラーとエラーメッセージを追加する.
				bindingResult.rejectValue("supplierId", "NotSupplier");
			}
		}

		// バリデーションエラーがあれば商品情報修正フォーム画面へ戻る.
		if (bindingResult.hasErrors()) {

			/* 商品IDを渡すために商品情報と入荷先情報をmodelに格納する処理(ProductWithSupplierなのは158行目・189行目・190行目をみる),
			 * 入荷先名全件取得しmodelに格納する処理・ヘッダーの設定をmodel格納する処理をまとめたメソッドを呼び出している(下のほうでprivateメソッドとして設定している). */
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

	/** 画像修正ボタンを押してくるところ */
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

	/** 画像修正フォームの確定ボタンを押してくるところ 
	 * @throws Exception */
	@PostMapping("/{productId}/info/imageEdit")
	public String postImageEdit(Model model, @PathVariable Integer productId,
			@ModelAttribute @Validated ImageEditForm form, BindingResult bindingResult){

		// 商品IDから商品情報を取得する(削除済みは除く).
		MProduct product = productInfoService.getOneProduct(productId);

		// 取得した商品情報が存在するか確認する(存在しなければエラー画面へ).
		if (product == null) {
			return "/error";
		}

		/* MultipartFile型はSpringのアップロードされたファイルを扱うためのオブジェクト.
		 * ファイル名・サイズ・MIMEタイプ(ファイルの種類を表す情報でタイプ/サブタイトルの形式(image/jpegみたいな)をしている)・内容（バイト配列）などをもつ. */
		MultipartFile file = form.getProductFile();
		String originalFileName = file.getOriginalFilename();
		System.out.println(111);
		System.out.println(originalFileName);
		System.out.println(222);
		System.out.println(file);

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
		System.out.println(productImageEdit.getProductImage());

		// 画像情報を更新する.
		//productInfoService.updateProductImage(product, productImageEdit);

		return "redirect:/products/" + productId + "/info/display-details";

	}

	/** 商品情報削除ボタンを押してくるところ */
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

	/** 商品情報削除フォーム画面の削除ボタンを押してくるところ */
	@PostMapping("/{productId}/info/delete")
	public String postDelete(Model model, @PathVariable Integer productId) {

		/// 商品IDから商品情報を取得する(削除済みは除く).
		MProduct productOne = productInfoService.getOneProduct(productId);

		// 取得した商品情報が存在するか確認する(存在しなければエラー画面へ).
		if (productOne == null) {
			return "/error";
		}

		// 商品情報の更新を行う.
		productInfoService.updateIsDeleted(productOne);

		return "redirect:/products/info/list";
	}

	/** 商品登録フォーム画面への遷移と商品登録フォーム画面で登録ボタンを押した後のバリデーションエラー時にフォーム画面に戻るときの共通処理をまとめたメソッド　*/
	private void goToRegister(Model model, RegisterForm form) {

		// 削除済み以外の入荷先名を取得し、modelに格納して画面に渡す.
		List<MSupplier> supplierList = productInfoService.getAllSupplier();
		model.addAttribute("supplierList", supplierList);

		// ヘッダーの色と項目を設定する.
		customHeader.setGray("商品登録");
		model.addAttribute("customHeader", customHeader);
	}

	/** 商品情報修正フォーム画面への遷移と商品情報修正フォーム画面で確定ボタンを押した後のバリデーションエラー時にフォームに戻るときの共通処理をまとめたメソッド　*/
	private void goToEdit(Model model, ProductWithSupplier productWithSupplier) {

		// 取得した商品情報と入荷情報をmodelに格納する.
		model.addAttribute("productWithSupplier", productWithSupplier);

		// 削除済み以外の入荷先名を全件取得し,modelに格納して画面に渡す.
		List<MSupplier> supplierList = productInfoService.getAllSupplier();
		model.addAttribute("supplierList", supplierList);

		// ヘッダーの色と項目を設定する.
		customHeader.setGray("商品修正");
		model.addAttribute("customHeader", customHeader);
	}

}
