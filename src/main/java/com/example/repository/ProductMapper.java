package com.example.repository;

import org.apache.ibatis.annotations.Mapper;

import com.example.domain.products.model.MProduct;

/**
* 商品情報を扱う Mapper.
* m_product テーブルの情報取得・登録・更新を行う.
* 
*/
@Mapper
public interface ProductMapper {
	
	/**  
	 * 商品番号から商品情報を取得する.
	 * 
	 * @param productNumber 商品番号.
	 * @return 商品情報.
	 */
	public MProduct findByProductNumber(String productNumber);
	
	/** 
	 * 商品情報を登録する.
	 * 
	 * @param product 登録する商品情報.
	 */
	public void insertOne(MProduct product);
	
	/** 
	 * 商品IDから商品情報を取得する(削除済みは除く).
	 * 
	 * @param  productId 取得する商品のID.
	 * @return 商品情報.
	 */
	public MProduct findByProductId(Integer productId);
	
	/** 
	 * 商品情報(商品番号・商品名・入荷先)を更新する. 
	 * 
	 * @param  product 更新する商品情報.
	 */
	public void updateOne(MProduct product);
	
	/** 
	 * 削除フラグを更新する. 
	 * 
	 * @param product 削除する商品情報.
	 */
	public void updateIsDeleted(MProduct product);
	
	/** 
	 * 商品の画像情報を更新する.
	 * 
	 * @param  product 更新する商品情報.
	 */
	public void updateProductImage(MProduct product);
	
}
