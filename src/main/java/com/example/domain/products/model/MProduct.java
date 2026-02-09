package com.example.domain.products.model;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * m_product テーブルに対応するクラス.
 * データベースの m_product テーブルの1レコード分の情報を保持する.
 * 
 */
@Data
public class MProduct {
	
	private Integer productId;								// 商品ID.
	private String productName;								// 商品名.
	private String productNumber;							// 商品番号.
	private Integer supplierId;								// 入荷先ID.
	private String productImage;							// 商品画像.
	private Integer productIsDeleted;						// 商品情報削除フラグ.
	private LocalDateTime productRegisterDateTime; 			// 登録日.
	private LocalDateTime productUpdateDateTime;			// 更新日.


}
