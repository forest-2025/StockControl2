package com.example.dto.products;

import lombok.Data;

@Data
public class ProductList {
	
	private Integer productId;			// 商品ID.
	private String productName;			// 商品名.
	private String productNumber;		// 商品番号.
	private Integer productIsDeleted;	// 商品削除フラグ.
	private Integer supplierId;			// 入荷先ID.
	private String productImage;		// 商品画像.
	private String supplierName;		// 入荷先名.
	private Integer supplierIsDeleted;	// 入荷先削除フラグ.
	private Integer stockId;			// 在庫ID.
	private Integer stockQuantity;		// 在庫数.

}
