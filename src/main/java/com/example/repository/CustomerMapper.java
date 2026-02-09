package com.example.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.domain.customers.model.MCustomer;

/**
 * 出荷先情報を扱う Mapper.
 * m_customer テーブルの情報取得・登録・更新を行う.
 * 
 */
@Mapper
public interface CustomerMapper {
	
	/** 
	 * 削除済み以外の出荷先一覧を出荷先IDの昇順で取得する.
	 * 
	 * @return 出荷先一覧.
	 */
	public List<MCustomer> findAllInAscById();
	
	/** 
	 * 削除済み以外の出荷先IDから出荷先情報を取得する.
	 * 
	 * @param customerId 取得する出荷先ID.
	 * @return 出荷先情報.
	 */
	public MCustomer findByCustomerId(Integer customerId);
	
	/** 
	 * 削除済み以外の出荷先検索結果一覧を取得する(出荷先ID・出荷先名・出荷先名ふりがなで検索する). 
	 * 
	 * @param search 検索語句.
	 * @param sortItem 並べ替え項目（IDまたはふりがな）.
	 * @param sort 並べ替え順序（昇順または降順）.
	 * @return 出荷先一覧.
	 */
	public List<MCustomer> findSearchResults(String search ,String sortItem,String sort);
	
	/** 
	 * 出荷先を登録する.
	 *  
	 * @param customer 登録する出荷先情報.
	 */
	public void insertOne(MCustomer customer);
	
	/** 
	 * 出荷先の情報を更新する.
	 * 
	 * @param customer 更新する出荷先情報.
	 */
	public void updateOne(MCustomer customer);
	
	/** 
	 * 出荷先の削除フラグを更新する.
	 *  
	 * @param customer 更新する出荷先情報.
	 */
	public void updateIsDeleted(MCustomer customer);
}
