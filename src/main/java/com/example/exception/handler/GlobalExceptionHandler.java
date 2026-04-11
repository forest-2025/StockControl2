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
	 * どの処理で失敗したかは例外クラス名とスタックトレースで確認できる.
	 * 基本的には商品画像のサイズが20MBをこえてもimage.jsでエラーメッセージを出して処理をとめるため,
	 * コントローラクラスにたどり着かないのでこのエラーが発生することはきわめて低い.
	 *
	 * @param ex 発生した例外オブジェクト.
	 * @param model ビューにデータを渡すためのモデル.
	 */
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public String handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex, Model model) {
		log.warn("画像のサイズが20MBを越えています: {}", ex.getMessage(), ex);
		String errorMessage = messageSource.getMessage("MaxUploadSizeMessage", null, Locale.JAPAN);
		model.addAttribute("errorMessage", errorMessage);

		return "error";
	}

}

/* MaxUploadSizeExceededExceptionについて
 * 基本的には商品画像のサイズが20MBをこえてもimage.jsでエラーメッセージを出して処理をとめるため,
 * コントローラクラスにたどり着かないのでこのエラーが発生することはきわめて低い.
 * また20MB~22.5MBぐらいまでならこのエラーになるがそれ以上のサイズだと接続を切るため,
 * ブラウザのネットワークが切断した等の画面に遷移し,この処理にたどり着かない.
 * 
 * それは,application.propertiesで,
 * 1.1ファイルの最大アップロードサイズ.
 * spring.servlet.multipart.max-file-size=20MB
 * 2.1リクエストの最大サイズ.
 * spring.servlet.multipart.max-request-size=25MB
 * と設定しているから.
 * 
 * 20MBをこえる画像ファイルが来たときTomcatはデフォルトで2MBまで,つまり20MBをこえた2MB分までならデータを読み捨て（受け取ってすぐ捨てる）てから接続を閉じるが,
 * それ以上だと接続を強制的に切断するため設定していたエラー画面に遷移せず,ブラウザ側の接続できていないことを示す画面に遷移する.
 * (なぜか,22.5MBの画像までは大丈夫だった)
 * 
 * この読み捨てるデータの設定は変更することができる.
 * 3.リクエストサイズ制限を超えた際にサーバーがどれだけのデータを読み捨て（受け取ってすぐ捨てる）てから接続を閉じるかの最大値を指定する.
 * server.tomcat.max-swallow-size=10MB
 * この設定はデフォルトで2MBで-1にすると無制限に読み捨てるが,大きいファイルを読み続けることで通信の占有されてしまったり,
 * わざと大きいファイルをおくることでサーバーをパンクさせるDoS攻撃（拒否攻撃）にあったりするため-1には設定しないほうがいい.
 * 
 * もし30MBの画像ファイルが来たとき,
 *  ①spring.servlet.multipart.max-file-size=20MB
 *  ②spring.servlet.multipart.max-request-size=35MB
 *  ③server.tomcat.max-swallow-size=10MB(ここが多めになってる?)
 * ならここにたどり着くが,
 * 
 *  ①spring.servlet.multipart.max-file-size=20MB
 *  ②spring.servlet.multipart.max-request-size=30MB(リクエスト全体が30MB以上になるためここで引っかかる)
 *  ③server.tomcat.max-swallow-size=11MB
 *  
 *  ①spring.servlet.multipart.max-file-size=20MB
 *  ②spring.servlet.multipart.max-request-size=35MB
 *  ③server.tomcat.max-swallow-size=9MB(ここが足りない)
 *  
 * だとならたどり着かず接続が切断される.
 * 
 * max-file-sizeのサイズを越えてエラーが確定して読み捨てがmax-swallow-size分はじまるが,
 * 読み捨て中にmax-request-sizeを越えるとエラーの割り込みがおこり接続が切断されるためこちらにこないので注意する.

 */
