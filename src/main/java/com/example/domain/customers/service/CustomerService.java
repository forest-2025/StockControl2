package com.example.domain.customers.service;

import com.example.domain.customers.model.MCustomer;
import com.github.pagehelper.PageInfo;

/**
 * 出荷先に関する業務処理を提供するサービス.
 * 
 */
public interface CustomerService {

	/** 
	 * 削除済み以外の出荷先一覧を出荷先IDの昇順でページングして取得する.
	 *
	 * @param page 何ページ目かを表すページ番号（1始まり）.
	 * @param size 1ページあたりの取得件数.
	 * @return 出荷先一覧のページ情報.
	 */
	public PageInfo<MCustomer> getSortItemInSortOrder(PageInfo<MCustomer> customerList,String search, String sortItem, String sort, int page);

	/** 
	 * 削除済み以外の出荷先一覧を出荷先IDの昇順でページングして取得する.
	 *
	 * @param page 何ページ目かを表すページ番号（1始まり）.
	 * @param size 1ページあたりの取得件数.
	 * @return 出荷先一覧のページ情報.
	 */
	public PageInfo<MCustomer> getAllInAscById(int page, int size);

	/** 
	 * 削除済み以外の出荷先一覧から検索語句が出荷先ID・出荷先名・出荷先名ふりがなと一致する出荷先を検索する.
	 * その一覧を,指定された並べ替え項目（IDまたはふりがな）と並べ替え順序（昇順または降順）に基づいてソートする.
	 * 
	 * @param page 何ページ目かを表すページ番号（1始まり）.
	 * @param size 1ページあたりの取得件数.
	 * @param search 検索語句.
	 * @param sortItem 並べ替え項目（IDまたはふりがな）.
	 * @param sort 並べ替え順序（昇順または降順）.
	 * @return 出荷先一覧のページ情報.
	 */
	public PageInfo<MCustomer> getSearchResults(String search, String sortItem, String sort, int page, int size);

	/** 
	 * 出荷先を登録する. 
	 * 
	 * @param customer 登録する出荷先情報.
	 */
	public void registerOne(MCustomer customer);

	/** 
	 * 出荷先IDから出荷先情報を取得する(削除済みは除く).
	 * 
	 * @param customerId 出荷先情報を取得する出荷先のID.
	 * @return 取得した出荷先情報.
	 */
	public MCustomer getByCustomerId(Integer customerId);

	/**
	 *  出荷先の情報を更新する.
	 *  
	 *  @param customer 更新する出荷先情報.*/
	public void updateOne(MCustomer customer);

	/** 
	 * 削除フラグを更新する.
	 * 出荷先情報の削除は物理削除ではなく論理削除のため削除フラグを1に設定する.
	 * 
	 * @param customer 更新する出荷先情報.*/
	public void updateIsDeleted(MCustomer customer);

}
