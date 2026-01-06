package com.example.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import org.apache.tika.Tika;
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
import com.example.domain.product.model.HistoryDetails;
import com.example.domain.products.model.MProduct;
import com.example.domain.products.model.ProductList;
import com.example.domain.products.model.ProductWithSupplier;
import com.example.domain.products.service.ProductInfoService;
import com.example.domain.suppliers.model.MSupplier;
import com.example.form.products.info.ImageEditForm;
import com.example.form.products.info.ProductEditForm;
import com.example.form.products.info.RegisterForm;

import net.coobird.thumbnailator.Thumbnails;

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

	/** 商品一覧画面（ホーム画面）へ遷移する. */
	@GetMapping("/info/list")
	public String getList(@RequestParam(required = false) String search, Model model) {

		/*@RequestParamのrequired属性をfalseにすることで検索パラメータ（URLの末尾の？に続く変数）の,
		 * パラメータ名searchがあってもなくても受け付けられるようにしている.
		 * パラメータ名searchが無ければ削除されていない全商品の一覧を取得し,あればsearchの値が含まれる商品を検索する.*/

		if (search == null) {
			List<ProductList> productList = productInfoService.getProductList();
			model.addAttribute("productList", productList);
		} else {
			List<ProductList> productList = productInfoService.getSearchProductList(search);
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
		System.out.println(file);

		// 画像ファイルがあれば一意のファイル名をつけるためスコープの外で宣言している(画像ファイルが無ければnullで登録されるため詳細画面で画像表示ボタンが表示されないようになる).
		String uniqueName = null;

		// 画像が選択されているか分岐する(登録フォームで<input type="file" name="multipartFile">は存在し,画像が選択されている(画像が空白でない)ということ).
		if (file != null && !file.isEmpty()) {

			/* InputStreamクラスは,バイト単位でデータを読み込むための抽象クラス.
			 * MultipartFileはインターフェースで,HTTPでアップロードされたファイルのことで,
			 * ファイル内容をバイトデータとして持っている.
			 * そのバイトデータをInputStreamを通して順次読み込む.
			 * つまりInputStreamはバイトデータを読み込む窓口のようなもので,
			 * InputStream inputStream = new BufferedInputStream(file.getInputStream())で,
			 * ファイルのデータを読み込む窓口（InputStreamオブジェクト）を生成しているだけでMultipartFileのバイトデータをBufferedInputStreamで扱う準備が整えている.
			 * BufferedInputStreamでラップすることで,元の InputStream をそのまま読み込むより便利になる.
			 * (内部にバッファがあるので少しずつではなくまとめて読み込めることで速くなる.
			 * またmark/resetに対応しているため,ファイルを少し読み込むとファイルの最初に戻れずにまた読み込みたいときにオブジェクトを破棄して再取得するということをしなくていい).
			 * tryの()はリソース宣言でリソース(InputStream)を安全に自動で閉じるために変数宣言＋初期化をおこなっている.
			 * (tryブロック終了後inputStream.close();を自動的に呼ぶように設定している).
			 * ここで宣言された変数のこと(inputStream)をリソースオブジェクトという.
			 * Javaのプログラムの中で外部との接続を行うとき,必ずその対象と接続して様々な処理をするためのオブジェクトを利用する.
			 * 処理が終了したらそのオブジェクト（接続）を閉じ（解放）なければならない.
			 * (ファイルの場合には接続したままでは他のプログラムが同じファイルを開くことが出来ないということが起こったり,接続できる上限数に達してしまって,
			 * 接続ができなくなるといったエラーになることがある). */
			try (InputStream inputStream = new BufferedInputStream(file.getInputStream())) {

				/* 選択された画像ファイルの大きさが20MB以内か確認する(20MBも大丈夫).
				 * 計算の単位はバイトなので注意する(1KB = 1024 バイト, 1MB = 1024KB = 1024 * 1024 バイト, 20MB = 20 * 1024 * 1024 バイト).
				 * 20MBより大きければエラーとエラーメッセージを追加する. */
				long maxSize = 20 * 1024 * 1024;
				if (file.getSize() > maxSize) {
					bindingResult.rejectValue("multipartFile", "OverSize");
				}

				// 画像の形式がJPEGかApache Tikaを使用して確認する（MIMEタイプをString型取得してから確認する）.
				Tika tika = new Tika();
				String mimeType = tika.detect(inputStream);

				// JPEGのMINEタイプ"image/jpeg"とおなじか確認し,違うときはエラーとエラーメッセージを追加する.
				if (!mimeType.equals("image/jpeg")) {
					bindingResult.rejectValue("productFile", "FileFormatsDiffer");
				}

				// エラーがあれば登録フォームに戻るため,保存先ディレクトリの確認や画像のリサイズの前に確認する.
				if (!bindingResult.hasErrors()) {

					/* Fileクラスは「ファイルやディレクトリのパス情報」を表すオブジェクト.
					 * 保存先のディレクトリ(ファイルを入れるフォルダのようなもの)のパス情報をもつオブジェクトを作成し保存先のディレクトリが存在するかどうかを確認し,
					 * 存在しなければディレクトリを作成する. */
					File dir = new File(uploadDir);
					if (!dir.exists()) { // java.lang.SecurityException(非チェック例外).
						dir.mkdirs(); // java.lang.SecurityException(非チェック例外).
					}

					/* UUIDでユニーク名を生成する(ユニバーサル・ユニーク・アイデンティファイアとは、全世界で重複しないように設計された128ビット長の一意な識別子(ID)のこと). 
					 * UUID.randomUUID()でランダムな一意のIDを取得し,toString()で文字列化したものにJPEGの拡張子をつけることで,
					 * 一意のファイル名を作成できる. */
					uniqueName = UUID.randomUUID().toString() + ".jpg";

					// 保存したいディレクトリパスと一意の名前にした保存したい画像ファイル名を組み合わせたFileクラスのオブジェクトを作成する.
					File dest = new File(uploadDir, uniqueName); // java.lang.NullPointerException(非チェック例外).

					/* 画像をリサイズする（最大幅800px, 最大高さ600px）.
					 * Thumbnails.of(in)でinputStreamを通してバイトデータを読み込む. */
					Thumbnails.of(inputStream) // NullPointerException/IllegalArgumentException/IOException(チェック例外).
							.size(800, 600) // このメソッドを何回も呼んだり,このメソッドの後にscale(double)メソッド(拡大縮小率の設定ができるメソッド)を呼ばなければ例外なし). 
							.toFile(dest); // 画像をリサイズしたものを作成し,Fileクラスのオブジェクトのディレクトリとファイル名でサーバ上に画像を保存する(パソコンの元画像を消しても表示できる).
				}

			} catch (Exception e) {

				e.printStackTrace();

				return "/error";
			}

		}

		// バリデーションエラーがあれば商品登録フォーム画面へ戻る.
		if (bindingResult.hasErrors()) {

			/* 入荷先名全件取得しmodelに格納する処理,ヘッダーの設定をmodelに格納する処理をまとめたメソッドを呼び出している(下のほうでprivateメソッドとして設定している). */
			this.goToRegister(model, form);

			return "/products/info/register";
		}

		// formクラスをエンティティクラスに変換する.
		MProduct product = modelMapper.map(form, MProduct.class);
		product.setProductImage(uniqueName);

		// 商品登録を行う.
		productInfoService.registerProduct(product);

		return "redirect:/products/info/list";

	}

	/** 詳細ボタンを押してくるところ. */
	@GetMapping("/{productId}/info/display-details")
	public String getDisplayDetails(Model model, @PathVariable Integer productId) {

		// 商品IDから商品情報を取得する(削除済みは除く).
		ProductList oneItem = productInfoService.getOneItemInTheList(productId);

		// 取得した商品情報が存在するか確認する(存在しなければエラー画面へ).
		if (oneItem == null) {
			return "/error";
		}

		// modelに格納する.
		model.addAttribute("oneItem", oneItem);

		// 商品IDからその商品の履歴を降順で取得してmodelに格納する.
		List<HistoryDetails> historyList = productInfoService.getHistoryForOneProduct(productId);
		model.addAttribute("historyList", historyList);

		// ヘッダーの色と項目を設定する.
		customHeader.setGray("詳細情報");
		model.addAttribute("customHeader", customHeader);

		return "/products/info/display-details";
	}

	//	/**  */
	//	@GetMapping("/{productId}/info/display-details/showImage")
	//	public String getDisplayDetailsImage(Model model, @PathVariable Integer productId) {
	//
	//		// 商品IDから商品情報を取得する(削除済みは除く).
	//		ProductList oneItem = productInfoService.getOneItemInTheList(productId);
	//
	//		// 取得した商品情報が存在するか確認する(存在しなければエラー画面へ).
	//		if (oneItem == null) {
	//			return "/error";
	//		}
	//
	//		// modelに格納する.
	//		model.addAttribute("oneItem", oneItem);
	//
	//		// 商品IDからその商品の履歴を降順で取得してmodelに格納する.
	//		List<HistoryDetails> historyList = productInfoService.getHistoryForOneProduct(productId);
	//		model.addAttribute("historyList", historyList);
	//
	//		// ヘッダーの色と項目を設定する.
	//		customHeader.setGray("詳細情報");
	//		model.addAttribute("customHeader", customHeader);
	//
	//		return "/products/info/display-details";
	//	}

	/** 商品情報修正ボタンを押してくるところ */
	@GetMapping("/{productId}/info/edit")
	public String getEdit(Model model, @PathVariable Integer productId, @ModelAttribute ProductEditForm form) {

		// 商品IDから商品情報と入荷先情報を取得する(削除済みは除く.初期値として入荷先名も表示したいためMProductではなくProductWithSupplierを使用している).
		ProductWithSupplier productWithSupplier = productInfoService.getOneProductWithSupplier(productId);
		System.out.println(productWithSupplier);
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
		ProductList oneItem = productInfoService.getOneItemInTheList(productId);

		// 取得した商品情報が存在するか確認する(存在しなければエラー画面へ).
		if (oneItem == null) {
			return "/error";
		}

		// modelに格納する.
		model.addAttribute("oneItem", oneItem);
		model.addAttribute("imageEditForm", form);

		// ヘッダーの色と項目を設定する.
		customHeader.setGray("画像修正");
		model.addAttribute("customHeader", customHeader);
		
		return "/products/info/image-edit";
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
