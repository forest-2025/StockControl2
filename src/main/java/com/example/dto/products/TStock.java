package com.example.dto.products;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * t_stock テーブルに対応するクラス.
 * データベースの t_stock テーブルの1レコード分の情報を保持する.
 * 
 */
@Data
public class TStock {
	
	private Integer stockId;								// 在庫ID.
	private Integer productId;								// 商品ID.
	private Integer stockQuantity;							// 在庫数.
	private LocalDateTime stockRegisterDateTime; 			// 登録日.
	private LocalDateTime stockUpdateDateTime;				// 更新日.

	/** 引数がproductIdとstockQuantityのコンストラクタ(商品登録時の初期在庫数登録で必要). */
	public TStock(Integer productId,Integer stockQuantity) {
		
		this.productId = productId;
		this.stockQuantity = stockQuantity;
	}

}
