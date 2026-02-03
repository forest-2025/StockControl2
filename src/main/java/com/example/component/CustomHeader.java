package com.example.component;

import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * ヘッダーの項目名と背景色を設定するクラス.
 *
 */
@Data
@Component
public class CustomHeader {
	private String headerItem;
	private String headerColor;

	/**
	 * 商品情報用ヘッダー.
	 * ヘッダーの項目名と背景色を設定する(背景色はグレー).
	 * 
	 * @param headerItem ヘッダーの項目名.
	 */
	public void setGray(String headerItem) {
		this.headerItem = headerItem;
		this.headerColor = "gray";
		
	}

	/**
	 * 入荷・入荷先一覧用ヘッダー.
	 * ヘッダーの項目名と背景色を設定する(背景色は赤色).
	 * 
	 * @param headerItem ヘッダーの項目名.
	 */
	public void setRed(String headerItem) {
		this.headerItem = headerItem;
		this.headerColor = "red";

	}

	/**
	 * 出荷・出荷先一覧用ヘッダー.
	 * ヘッダーの項目名と背景色を設定する(背景色は青色).
	 * 
	 * @param headerItem ヘッダーの項目名.
	 */
	public void setBlue(String headerItem) {
		this.headerItem = headerItem;
		this.headerColor = "blue";
	}
	
	/**
	 * ユーザー一覧用ヘッダー.
	 * ヘッダーの項目名と背景色を設定する(背景色は黄色).
	 * 
	 * @param headerItem ヘッダーの項目名.
	 */
	public void setYellow(String headerItem) {
		this.headerItem = headerItem;
		this.headerColor = "yellow";
	}
	
}
