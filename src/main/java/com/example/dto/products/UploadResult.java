package com.example.dto.products;

import java.util.List;

import lombok.Data;

/**
 * 画像処理時のバリデーションエラーメッセージのリストと,データベースに保存するためのファイル名を保持するクラス.
 * 
 */
@Data
public class UploadResult {

	private List<String> errors; 	// バリデーションエラーメッセージのリスト.
	private String fileName; 		// DB に保存するためのファイル名.

	/**
	 * 空のオブジェクトを作成するコンストラクタ.
	 * 
	 */
	public UploadResult() {

	}

	/**
	 * バリデーションエラーメッセージのリストが存在しているか,また空でないか確認する.
	 * 
	 * @return 	バリデーションエラーメッセージのリストが存在し,空じゃない: true.
	 * 			バリデーションエラーメッセージのリストが存在しない,または空: false.
	 */
	public boolean hasErrors() {

		return errors != null && !errors.isEmpty();
	}
}
