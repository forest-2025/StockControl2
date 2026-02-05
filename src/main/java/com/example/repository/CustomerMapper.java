package com.example.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.dto.customers.MCustomer;

@Mapper
public interface CustomerMapper {
	
	/** 削除済み以外の出荷先一覧を出荷先IDの昇順で取得する. */
	public List<MCustomer> findAllInAscById();
	
	/** 削除済み以外の出荷先IDから出荷先情報を取得する */
	public MCustomer findByCustomerId(Integer customerId);
	
	/** 削除済み以外の出荷先検索結果一覧を取得する(出荷先ID・出荷先名・出荷先名ふりがなで検索する). */
	public List<MCustomer> findSearchResults(String search ,String sortItem,String sort);
	
	/** 出荷先を登録する. */
	public void insertOne(MCustomer customer);
	
	/** 出荷先IDで指定した出荷先の情報を更新する. */
	public void updateOne(MCustomer customer);
	
	/** 出荷先IDで指定した出荷先の削除フラグを更新する. */
	public void updateIsDeleted(MCustomer customer);
}
