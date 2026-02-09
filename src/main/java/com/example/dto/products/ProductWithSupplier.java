package com.example.dto.products;

import com.example.domain.products.model.MProduct;
import com.example.domain.suppliers.model.MSupplier;

import lombok.Data;

/**
 * 商品情報と入荷先情報のテーブルを結合したクラス.
 * m_product, m_supplier をまとめている.
 * 商品情報と,とくに入荷先名が必要な時に利用する.
 * 
 */
@Data
public class ProductWithSupplier {
	
	private MProduct product;
	private MSupplier supplier; 

}
