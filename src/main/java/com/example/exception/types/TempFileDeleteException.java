package com.example.exception.types;

/**
 * 一時ファイルの削除に失敗した場合に投げる例外.
 * 
 */
public class TempFileDeleteException extends RuntimeException {

	/** 
	  * メッセージだけで例外を作るコンストラクタ.
	  * 
	  * @param message 例外メッセージ.
	  */
	public TempFileDeleteException(String message) {
		super(message);
	}

	/** 
	 * メッセージと原因の例外をセットして作るコンストラクタ.
	 * 
	 * @param message 例外メッセージ.
	 * @param cause 原因となった例外.
	 */
	public TempFileDeleteException(String message, Throwable cause) {
		super(message, cause);
	}
}
