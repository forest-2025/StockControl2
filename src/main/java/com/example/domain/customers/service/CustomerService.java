package com.example.domain.customers.service;

import com.example.domain.customers.model.MCustomer;
import com.github.pagehelper.PageInfo;

public interface CustomerService {

	/** 削除済み以外の出荷先一覧を出荷先IDの昇順で取得する. */
	public PageInfo<MCustomer> getAllInAscById(int page, int size);

	/** 削除済み以外の出荷先検索結果一覧を取得する(出荷先ID・出荷先名・出荷先名ふりがなで検索する). */
	public PageInfo<MCustomer> getSearchResults(int page, int size, String search, String sortItem, String sort);

	/** 出荷先を登録する. */
	public void registerOne(MCustomer customer);

	/** 出荷先IDから出荷先情報を取得する. */
	public MCustomer getByCustomerId(Integer customerId);

	/** 出荷先IDで指定した出荷先の情報を更新する. */
	public void updateOne(MCustomer customer);

	/** 出荷先IDで指定した削除フラグを更新する. */
	public void updateIsDeleted(MCustomer customer);

}
