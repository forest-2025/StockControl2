package com.example.exception.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.HandlerMapping;

import com.example.component.CustomHeader;
import com.example.domain.products.model.MProduct;
import com.example.domain.products.service.ProductInfoService;
import com.example.domain.suppliers.model.MSupplier;
import com.example.dto.products.UploadResult;
import com.example.exception.types.ImageProcessingException;
import com.example.exception.types.TempFileDeleteException;
import com.example.form.products.info.RegisterForm;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * 画像処理での例外を処理するハンドラークラス.
 * 例外が発生したらログに出力し共通エラー画面に遷移する.
 * 
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ProductInfoService productInfoService;

	/**
	 * DataAccessException とそのサブクラスをキャッチしてログに出力する.
	 * どの処理で失敗したかは例外クラス名とスタックトレースで確認できる.
	 *
	 * @param ex 発生した例外オブジェクト.
	 */
	@ExceptionHandler(DataAccessException.class)
	public String handleDataAccessException(DataAccessException ex) {
		// ex.getClass().getSimpleName()で例外のクラス名が取得されて{}に埋め込まれて表示される.
		log.error("データベース処理例外発生: {}", ex.getClass().getSimpleName(), ex);
		return "error";

	}

	/**
	 * Exception とそのサブクラスをキャッチしてログに出力する.
	 * どの処理で失敗したかは例外クラス名とスタックトレースで確認できる.
	 *
	 * @param ex 発生した例外オブジェクト.
	 */
	@ExceptionHandler(Exception.class)
	public String handleException(Exception ex) {

		log.error("予期せぬエラーの発生: {}", ex.getClass().getSimpleName(), ex);
		return "error";
	}

	/**
	 * ImageProcessingException とそのサブクラスをキャッチしてログに出力する.
	 * どの処理で失敗したかは例外クラス名とスタックトレースで確認できる.
	 *
	 * @param ex 発生した例外オブジェクト.
	 */
	@ExceptionHandler(ImageProcessingException.class)
	public String handleImageProcessingException(ImageProcessingException ex) {

		log.error("画像処理例外発生: {}", ex.getClass().getSimpleName(), ex);
		return "error";
	}

	/**
	 * 一時ファイル削除失敗の例外をまとめてログ出力する.
	 *
	 * @param ex 発生した例外オブジェクト.
	 */
	@ExceptionHandler(TempFileDeleteException.class)
	public String handleTempFileDeleteException(TempFileDeleteException ex) {
		log.warn("一時ファイル削除失敗: {}", ex.getMessage(), ex);
		return "error";
	}

	/**
	 * Exception とそのサブクラスをキャッチしてログに出力する.
	 * どの処理で失敗したかは例外クラス名とスタックトレースで確認できる.
	 *
	 * @param ex 発生した例外オブジェクト.
	 */
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public String handleException(MaxUploadSizeExceededException ex, HttpServletRequest request, Model model) {

		log.error("画像サイズが20MBを越えています", ex);
		List<String> errors = new ArrayList<>();
		String errorMessage = messageSource.getMessage("OverSize", null, Locale.JAPAN);
		errors.add(errorMessage);

		UploadResult result = new UploadResult();
		result.setErrors(errors);
		System.out.println(result);
		model.addAttribute("errors", result.getErrors());
		System.out.println(result.getErrors());

		// ヘッダーの色と項目を設定する.
		CustomHeader customHeader = new CustomHeader();

		// 削除済み以外の入荷先名を取得し、modelに格納して画面に渡す.
		List<MSupplier> supplierList = productInfoService.getAllSupplier();
		model.addAttribute("supplierList", supplierList);

		String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
		
		 // 2. パス変数の Map を取得
	    @SuppressWarnings("unchecked")
	    Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		if ("/products/info/register".equals(pattern)) {

			customHeader.setGray("商品登録");
			model.addAttribute("customHeader", customHeader);
			RegisterForm registerForm = new RegisterForm();
			model.addAttribute("registerForm", registerForm);
			return "products/info/register";
			
		} else if ("/products/{productId}/info/imageEdit".equals(pattern)) {
			Integer productId = Integer.parseInt(pathVariables.get("productId"));
			// 商品IDから商品情報を取得する(削除済みは除く).
			MProduct product = productInfoService.getOneProduct(productId);
//			try {
//	            // String から Integer へ変換
//	            Integer userId = Integer.parseInt(pathVariables.get("userId"));
//
//	            // 数値（ID）に応じた個別のハンドリング
//	            if (userId < 1000) {
//	                return ResponseEntity.status(413).body("特別ユーザー様: ファイルが大きすぎます");
//	            }
//	        } catch (NumberFormatException e) {
//	            // IDが数値でなかった場合のフォールバック（通常は起こりにくい）
//	        }
//	    }
//
//			// 取得した商品情報が存在するか確認する(存在しなければエラー画面へ).
//			if (product == null) {
//				return "error";
//			}
//			model.addAttribute("product", product);
//			model.addAttribute("errors", result.getErrors());
//
//			// ヘッダーの色と項目を設定する.
//			customHeader.setGray("画像修正");
//			model.addAttribute("customHeader", customHeader);
//
//			return "products/info/image-edit";
//		}
		return null;
		
		
	}

}
