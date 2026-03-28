package com.example.exception.handler;

import org.springframework.dao.DataAccessException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import lombok.extern.slf4j.Slf4j;

/**
 * 例外を処理するハンドラークラス.
 * 例外が発生したらログに出力し共通エラー画面に遷移する.
 * 
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	/**
	 * DataAccessException とそのサブクラスをキャッチしてログに出力する.
	 * どの処理で失敗したかは例外クラス名とスタックトレースで確認できる.
	 *
	 * @param ex 発生した例外オブジェクト.
	 * @param model ビューにデータを渡すためのモデル.
	 */
	@ExceptionHandler(DataAccessException.class)
	public String handleDataAccessException(DataAccessException ex, Model model) {
		// ex.getClass().getSimpleName()で例外のクラス名が取得されて{}に埋め込まれて表示される.
		log.error("データベース処理例外発生: {}", ex.getClass().getSimpleName(), ex);
		model.addAttribute("error", "");
		return "error";

	}

	/**
	 * MaxUploadSizeExceededException をキャッチしてログに出力する.
	 * どの処理で失敗したかは例外クラス名とスタックトレースで確認できる.
	 *
	 * @param ex 発生した例外オブジェクト.
	 * @param model ビューにデータを渡すためのモデル.
	 */
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public String handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex, Model model) {
		log.warn("画像のサイズが20MBを越えています: {}", ex.getMessage(), ex);
		model.addAttribute("error", "MaxUploadSize");

		return "error";
	}

	/**
	 * Exception とそのサブクラスをキャッチしてログに出力する.
	 * どの処理で失敗したかは例外クラス名とスタックトレースで確認できる.
	 *
	 * @param ex 発生した例外オブジェクト.
	 * @param model ビューにデータを渡すためのモデル.
	 */
	@ExceptionHandler(Exception.class)
	public String handleException(Exception ex, Model model) {
		log.error("予期せぬエラーの発生: {}", ex.getClass().getSimpleName(), ex);
		model.addAttribute("error", "");

		return "error";
	}

}
