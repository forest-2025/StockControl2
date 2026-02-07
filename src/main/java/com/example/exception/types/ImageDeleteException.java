package com.example.exception.types;

/**
 * 画像削除処理で発生する例外のクラス.
 * 
 */
public class ImageDeleteException extends ImageProcessingException {

	/** 
	  * メッセージだけで例外を作るコンストラクタ.
	  * 
	  * @param message 例外メッセージ.
	  */
	public ImageDeleteException(String message) {
		super(message);
	}

	/** 
	 * メッセージと原因の例外をセットして作るコンストラクタ.
	 * 
	 * @param message 例外メッセージ.
	 * @param cause 原因となった例外.
	 */
	public ImageDeleteException(String message, Throwable cause) {
		super(message, cause);
	}
}
