package com.example.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.domain.suppliers.model.MSupplier;

/**
 * 入荷先情報を扱う Mapper.
 * m_supplier テーブルの情報取得・登録・更新を行う.
 * 
 */
@Mapper
public interface SupplierMapper {
	
	/** 
	 * 削除済み以外の入荷先一覧を入荷先IDの昇順で取得する.
	 * 
	 * @return 入荷先一覧. 
	 */
	public List<MSupplier> findAllInAscById();
	
	/** 
	 * 入荷先IDから入荷先情報を取得する(削除済みは除く).
	 * 
	 * @param supplierId 取得する入荷先ID.
	 * @return 入荷先情報.
	 */
	public MSupplier findBySupplierId(Integer supplierId);

	/** 
	 * 削除済み以外の入荷先検索結果一覧を取得する(入荷先ID・入荷先名・入荷先名ふりがなで検索する).
	 * 
	 * @param search 検索語句.
	 * @param sortItem 並べ替え項目（IDまたはふりがな）.
	 * @param sort 並べ替え順序（昇順または降順）.
	 * @return 入荷先一覧. 
	 */
	public List<MSupplier> findSearchResults(String search,String sortItem,String sort);
	
	/** 
	 * 入荷先を登録する.
	 * 
	 * @param supplier 登録する入荷先情報.
	 */
	public void insertOne(MSupplier supplier);
	
	/** 
	 * 入荷先の情報を更新する.
	 * 
	 * @param supplier 更新する入荷先情報.
	 */
	public void updateOne(MSupplier supplier);
	
	/** 
	 * 入荷先の削除フラグを更新する.
	 * 
	 * @param supplier 更新する入荷先情報.
	 */
	public void updateIsDeleted(MSupplier supplier);
}
