package com.example.exception.types;

/**
 * 画像登録処理で発生する例外のクラス.
 * 
 */
public class ImageRegisterException extends ImageProcessingException {

	/** 
	  * メッセージだけで例外を作るコンストラクタ.
	  * 
	  * @param message 例外メッセージ.
	  */
	public ImageRegisterException(String message) {
		super(message);
	}

	/** 
	 * メッセージと原因の例外をセットして作るコンストラクタ.
	 * 
	 * @param message 例外メッセージ.
	 * @param cause 原因となった例外.
	 */
	public ImageRegisterException(String message, Throwable cause) {
		super(message, cause);
	}
}
