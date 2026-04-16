package com.example.exception.handler;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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

	@Autowired
	private MessageSource messageSource;

	/**
	 * MaxUploadSizeExceededException をキャッチしてログに出力する.
	 * 画像ファイルは20MB以下しか登録できないが,
	 * 30MBまでは受け付けられるようにapplication.propertiesで設定している.
	 * そのため30MBを越えるとこちらからエラー画面に遷移する.
	 *
	 * @param ex 発生した例外オブジェクト.
	 * @param model ビューにデータを渡すためのモデル.
	 */
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public String handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex, Model model) {

		log.warn("画像のサイズが30MBを越えています: {}", ex.getMessage(), ex);
		String errorMessage = messageSource.getMessage("maxUploadSizeMessage", null, Locale.JAPAN);
		model.addAttribute("errorMessage", errorMessage);

		return "error";
	}

}