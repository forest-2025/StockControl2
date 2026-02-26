package com.example.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.dto.products.ProductList;

/**
 * 商品一覧の情報を取得できる Mapper.
 * m_product ・ m_supplier ・ t_stock テーブルのうち,商品一覧画面に必要なカラムを結合している.
 * 読み取り専用で DB への更新は行わない.
 * 
 */
@Mapper
public interface ProductListMapper {
	
	/** 
	 * 削除済み以外の商品一覧を商品番号の昇順で取得する.
	 * 
	 * @return 商品一覧.
	 */
	public List<ProductList> findAll();
	
	/** 
	 * 削除済み以外の商品検索結果一覧を商品番号の指定された並び替え順序で取得する(商品番号・商品名・入荷先名・入荷先ふりがなで検索する).
	 * 
	 * @param search 検索語句.
	 * @param sort 並べ替え順序（昇順または降順）.
	 * @return 商品一覧.
	 */
	public List<ProductList> findSearchResults(String search,String sort);
	
	/** 
	 * 削除済み以外の商品IDから商品情報を商品番号の昇順で取得する.
	 * 
	 * @param productId 取得する商品のID.
	 * @return 商品一覧.
	 */
	public ProductList findByProductId(Integer productId);


}
