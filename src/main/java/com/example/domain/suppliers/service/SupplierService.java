package com.example.domain.suppliers.service;

import com.example.domain.suppliers.model.MSupplier;
import com.github.pagehelper.PageInfo;

/**
 * 入荷先に関する業務処理を提供するサービス.
 * 
 */
public interface SupplierService {

	/** 
	 * 指定された並べ替え項目（IDまたはふりがな）と並べ替え順序（昇順または降順）に基づいて分岐して,
	 * 適切な削除済み以外の入荷先一覧を取得する.
	 *
	 * @param search 検索語句.
	 * @param sortItem 並べ替え項目（IDまたはふりがな）.
	 * @param sort 並べ替え順序（昇順または降順）.
	 * @param page 何ページ目かを表すページ番号（1始まり）.
	 * @return 入荷先一覧のページ情報.
	 */
	public PageInfo<MSupplier> findAllSorted(String search, String sortItem, String sort, int page);

	/** 
	 * 削除済み以外の入荷先一覧を入荷先IDの昇順でページングして取得する.
	 * 
	 * @param page 何ページ目かを表すページ番号（1始まり）.
	 * @return 入荷先一覧のページ情報.
	 */
	public PageInfo<MSupplier> getAllInAscById(int page);

	/**
	 * 削除済み以外の入荷先一覧から検索語句が入荷先ID・入荷先名・入荷先名ふりがなと一致する入荷先を検索する.
	 * その一覧を,指定された並べ替え項目（IDまたはふりがな）と並べ替え順序（昇順または降順）に基づいてソートする.
	 * 
	 * @param search 検索語句.
	 * @param sortItem 並べ替え項目（IDまたはふりがな）.
	 * @param sort 並べ替え順序（昇順または降順）.
	 * @param page 何ページ目かを表すページ番号（1始まり）.
	 * @return 入荷先一覧のページ情報.
	 */
	public PageInfo<MSupplier> getSearchResults(String search, String sortItem, String sort, int page);

	/** 
	 * 入荷先を登録する.
	 * 
	 * @param supplier 登録する入荷先情報.
	 */
	public void registerOne(MSupplier supplier);

	/** 
	 * 入荷先IDから入荷先情報を取得する(削除済みは除く).
	 * 
	 * @param supplierId 入荷先情報を取得する入荷先ID.
	 * @return 取得した入荷先情報.
	 */
	public MSupplier getBySupplierId(Integer supplierId);

	/** 
	 * 入荷先の情報を更新する.
	 * 
	 * @param supplier 更新する入荷先情報.
	 */
	public void updateOne(MSupplier supplier);

	/** 
	 * 削除フラグを更新する.
	 * 入荷先情報の削除は物理削除ではなく論理削除のため削除フラグを1に設定する.
	 * 
	 * @param supplier 更新する入荷先情報.
	 */
	public void updateIsDeleted(MSupplier supplier);
}
