package com.example.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.dto.common.MSupplier;

@Mapper
public interface SupplierMapper {
	
	/** 削除済み以外の入荷先一覧を入荷先IDの昇順で取得する. */
	public List<MSupplier> findAllInAscById();
	
	/** 入荷先IDから入荷先情報を取得する(削除済みは除く).  */
	public MSupplier findBySupplierId(Integer supplierId);

	/** 削除済み以外の入荷先検索結果一覧を取得する(入荷先ID・入荷先名・入荷先名ふりがなで検索する). */
	public List<MSupplier> findSearchResults(String search,String sortItem,String sort);
	
	/** 入荷先を登録する. */
	public void insertOne(MSupplier supplier);
	
	/** 入荷先IDで指定した入荷先の情報を更新する. */
	public void updateOne(MSupplier supplier);
	
	/** 入荷先IDで指定した入荷先の削除フラグを更新する. */
	public void updateIsDeleted(MSupplier supplier);
}
