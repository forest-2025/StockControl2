package com.example.exception.handler;

import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.example.domain.products.service.ProductInfoService;
import com.example.exception.types.ImageProcessingException;
import com.example.exception.types.TempFileDeleteException;

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
		String errorMessage = messageSource.getMessage("OverSize", null, Locale.JAPAN);
		model.addAttribute("errorMessage",errorMessage);
//		String errorMessage = messageSource.getMessage("OverSize", null, Locale.JAPAN);
//
//		model.addAttribute("errors", errorMessage);
//
//		// ヘッダーの色と項目を設定する.
//		CustomHeader customHeader = new CustomHeader();
//
//		// 削除済み以外の入荷先名を取得し、modelに格納して画面に渡す.
//		List<MSupplier> supplierList = productInfoService.getAllSupplier();
//		model.addAttribute("supplierList", supplierList);
//
//		String uri = request.getRequestURI(); // 例: /users/123/upload
//
//		System.out.println(uri);
//		if ("/products/info/register".equals(uri)) {
//
//			customHeader.setGray("商品登録");
//			model.addAttribute("customHeader", customHeader);
//			RegisterForm registerForm = new RegisterForm();
//			model.addAttribute("registerForm", registerForm);
//			return "products/info/register";
//
//		}
//
//		String pattern = "/products/{productId}/info/imageEdit"; // 判定したいパターン
//
//		AntPathMatcher matcher = new AntPathMatcher();
//
//		if (matcher.match(pattern, uri)) {
//			// パス変数部分を Map として抽出
//			Map<String, String> variables = matcher.extractUriTemplateVariables(pattern, uri);
//			String productId = variables.get("productId"); // "123" が取得できる
//
//			try {
//				// String から Integer へ変換
//				Integer productIntegerId = Integer.parseInt(productId);
//
//				// 商品IDから商品情報を取得する(削除済みは除く).
//				MProduct product = productInfoService.getOneProduct(productIntegerId);
//				// 取得した商品情報が存在するか確認する(存在しなければエラー画面へ).
//				if (product == null) {
//					return "error";
//				}
//
//				model.addAttribute("product", product);
//
//				// ヘッダーの色と項目を設定する.
//				customHeader.setGray("画像修正");
//				model.addAttribute("customHeader", customHeader);
//
//				return "products/info/image-edit";
//
//			} catch (NumberFormatException e) {
//				// IDが数値でなかった場合のフォールバック（通常は起こりにくい）
//				log.error("商品IDが数値ではありません: {}", e.getMessage(), e);
//				return "error";
//			}
//		}
//		System.out.println("ここかな？");
		return "error";

	}

}
