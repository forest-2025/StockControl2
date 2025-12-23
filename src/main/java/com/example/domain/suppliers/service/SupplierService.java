package com.example.domain.suppliers.service;

import java.util.List;

import com.example.domain.suppliers.model.MSupplier;

public interface SupplierService {
	
	/** 入荷先一覧を入荷先IDの昇順で取得する. */
	public List<MSupplier> getAllInAscById();
	
	/** 入荷先検索結果一覧を取得する(入荷先ID・入荷先名・入荷先名ふりがなで検索する). */
	public List<MSupplier> getSearchResults(String search ,String sortItem,String sort);
	
	/** 入荷先を登録する. */
	public void registerOne(MSupplier supplier);

	/** 入荷先IDから入荷先情報を取得する(削除済みは除く). */
	public MSupplier getBySupplierId(Integer supplierId);
	
	/** 入荷先IDで指定した入荷先の情報を更新する. */
	public void updateOne(MSupplier supplier);
	
	/** 入荷先IDで指定した削除フラグを更新する. */
	public void updateIsDeleted(MSupplier supplier);
}
