package com.example.exception.handler;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.exception.types.ImageProcessingException;
import com.example.exception.types.TempFileDeleteException;

import lombok.extern.slf4j.Slf4j;

/**
 * 画像処理での例外を処理するハンドラークラス.
 * 例外が発生したらログに出力するだけで,ページ遷移は Spring MVC のデフォルトのエラー処理に任せている.
 * 
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	
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
