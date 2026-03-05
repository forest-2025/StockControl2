package com.example.exception.handler;

import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
		return "/error";
		
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
		return "/error";
	}
	
	/**
     * 一時ファイル削除失敗の例外をまとめてログ出力する.
     *
     * @param ex 発生した例外オブジェクト.
     */
    @ExceptionHandler(TempFileDeleteException.class)
    public String handleTempFileDeleteException(TempFileDeleteException ex) {
        log.warn("一時ファイル削除失敗: {}", ex.getMessage(), ex);
        return "/error";
    }

}
