package com.example.domain.suppliers.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.suppliers.model.MSupplier;
import com.example.domain.suppliers.service.SupplierService;
import com.example.repository.SupplierMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

/**
 * SupplierService の実装クラス.
 * 
 */
@Service
@Transactional
public class SupplierServiceImpl implements SupplierService {

	@Autowired
	private SupplierMapper supplierMapper;

	// 1ページで表示する入荷先情報を10に設定する.
	private static final int SHOW_SIZE = 10;

	// 指定された並べ替え項目（IDまたはふりがな）と並べ替え順序（昇順または降順）に基づいて分岐して,適切な削除済み以外の入荷先一覧を取得する.
	public PageInfo<MSupplier> findAllSorted(String search, String sortItem, String sort, int page) {

		// 入荷先一覧画面に遷移したとき(削除済み以外の入荷先一覧を入荷先IDの昇順で取得する).
		if (search == null) {

			return this.getAllInAscById(page);

			/* 検索ボタン,各種昇順・降順ボタンを押したときのsearchには,検索フォームに何も入っていなければ空白が入るのでnullではないためこちらに分岐する.
			 * sortItem(並び替え項目)がidまたはfuriganaか確認する. */
		} else if (sortItem.equals("id") || sortItem.equals("furigana")) {

			// sort(並び替え順序)がascかdescか確認する.
			if (sort.equals("asc") || sort.equals("desc")) {

				return this.getSearchResults(search, sortItem, sort, page);

				/* sort(並び替え順序)がascまたはdescでないとき(開発者ツールでクエリパラメータで値を変えられたときなど)は,
				 * 削除済み以外の入荷先情報を入荷先IDで昇順に並べた入荷先一覧を取得する. */
			} else {

				return this.getAllInAscById(page);

			}

			/* sortItem(並べ替え項目)がidやfuriganaでないとき(開発者ツールでクエリパラメータで値を変えられたときなど)は,
			 * 削除済み以外の入荷先情報を入荷先IDで昇順に並べた入荷先一覧を取得する. */
		} else {

			return this.getAllInAscById(page);

		}
	}

	// 削除済み以外の入荷先一覧を入荷先IDの昇順で取得する.
	@Override
	public PageInfo<MSupplier> getAllInAscById(int page) {

		PageHelper.startPage(page, SHOW_SIZE);
		List<MSupplier> supplierList = supplierMapper.findAllInAscById();

		return new PageInfo<>(supplierList);
	}

	// 削除済み以外の入荷先検索結果一覧を取得(入荷先ID・入荷先名・入荷先名ふりがなで検索)し,指定した項目と順序でソートする. 
	@Override
	public PageInfo<MSupplier> getSearchResults(String search, String sortItem, String sort, int page) {

		PageHelper.startPage(page, SHOW_SIZE);
		List<MSupplier> supplierList = supplierMapper.findSearchResults(search, sortItem, sort);

		return new PageInfo<>(supplierList);
	}

	// 入荷先を登録する.
	@Override
	public void registerOne(MSupplier supplier) {

		supplierMapper.insertOne(supplier);

	}

	// 入荷先IDから入荷先情報を取得する(削除済みは除く).
	@Override
	public MSupplier getBySupplierId(Integer supplierId) {

		MSupplier supplier = supplierMapper.findBySupplierId(supplierId);

		return supplier;
	}

	// 入荷先の情報を更新する.
	@Override
	public void updateOne(MSupplier supplier) {

		supplierMapper.updateOne(supplier);

	}

	// 削除フラグを更新する.
	@Override
	public void updateIsDeleted(MSupplier supplier) {

		// 入荷先情報の削除は物理削除ではなく論理削除のため,削除フラグ(is_deleted)を削除済みの1に変更する.
		supplier.setSupplierIsDeleted(1);
		// 削除フラグを更新する.
		supplierMapper.updateIsDeleted(supplier);

	}

}
