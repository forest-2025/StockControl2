package com.example.component;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class CustomHeader {
	private String headerItem;
	private String headerColor;

	// 商品情報用ヘッダー.
	public void setGray(String headerItem) {
		this.headerItem = headerItem;
		this.headerColor = "gray";
		
	}

	// 入荷・入荷先一覧用ヘッダー.
	public void setRed(String headerItem) {
		this.headerItem = headerItem;
		this.headerColor = "red";

	}

	// 出荷・出荷先一覧用ヘッダー.
	public void setBlue(String headerItem) {
		this.headerItem = headerItem;
		this.headerColor = "blue";
	}
	
	// ユーザー一覧用ヘッダー.
	public void setYellow(String headerItem) {
		this.headerItem = headerItem;
		this.headerColor = "yellow";
	}
	
}
