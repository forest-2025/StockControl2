package com.example.exception.types;

/**
 * 画像処理で発生する共通の例外クラス(スーパークラス).
 * 画像登録や更新処理でエラーが起きたときに使う.
 * 
 */
public class ImageProcessingException extends RuntimeException {

	/** 
	 * メッセージだけで例外を作るコンストラクタ.
	 * 
	 * @param message 例外メッセージ.
	 */
	public ImageProcessingException(String message) {
		super(message);
	}

	/** 
	 * メッセージと原因の例外をセットして作るコンストラクタ.
	 * 
	 * @param message 例外メッセージ.
	 * @param cause 原因となった例外.
	 */
	public ImageProcessingException(String message, Throwable cause) {
		super(message, cause);
	}

}
/* スーパークラスを作ることで「共通の型」としてまとめて扱えるようになる.
 * GlobalExceptionHandler でこのクラス1つを対象にしたメソッドを作成するだけで,
 * このクラスを継承したサブクラス()共通のメソッドとなる.
 * (サブクラス1つずつメソッドを作成しなくてよくなる.ただし,共通の同じ処理になる).
 * 
 *  コンストラクタが2つあるのは,
 *  	例外の原因が単純なメッセージだけで,
 *  	if (file.getSize() > MAX_SIZE) {
 *    		throw new ImageRegisterException("ファイルサイズが大きすぎます");
 *    	}
 *    	のように例外オブジェクトがないときに使用できるようにするため.
 *    
 *    	また,
 *    	} catch (IOException e) {
 *    		throw new ImageRegisterException("画像登録に失敗しました", e);
 *    	}
 *    	のように元の例外（IOException など）オブジェクトをラップしたいときなど,場合によって柔軟に対応できるようにしておくため.
 */
