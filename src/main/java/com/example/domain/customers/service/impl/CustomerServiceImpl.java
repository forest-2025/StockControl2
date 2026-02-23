package com.example.domain.customers.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.customers.model.MCustomer;
import com.example.domain.customers.service.CustomerService;
import com.example.repository.CustomerMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

/**
 * CustomerService の実装クラス.
 * 
 */
@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private CustomerMapper customerMapper;

	// 1ページで表示する入荷先情報を10に設定する.
	private static final int SHOW_SIZE = 10;

	// 指定された並べ替え項目（IDまたはふりがな）と並べ替え順序（昇順または降順）に基づいて分岐して,適切な削除済み以外の出荷先一覧を取得する.
	public PageInfo<MCustomer> findAllSorted(String search, String sortItem,
			String sort, int page) {

		if (search == null) {
			return this.getAllInAscById(page);

			/* 検索ボタン,各種昇順・降順ボタンを押したときのsearchには,検索フォームに何も入っていなければ空白が入るのでnullではないためこちらに分岐する.
			 * sortItem(並び替え項目)がidまたはfuriganaか確認する. */
		} else if (sortItem.equals("id") || sortItem.equals("furigana")) {

			// sort(並び替え順序)がascかdescか確認する.
			if (sort.equals("asc") || sort.equals("desc")) {
				return this.getSearchResults(search, sortItem, sort, page);

				/* sort(並び替え順序)がascまたはdescでないとき(開発者ツールでクエリパラメータで値を変えられたときなど)は,
				 * 削除済み以外の入荷先情報を出荷先IDで昇順に並べた入荷先一覧を取得する. */
			} else {
				return this.getAllInAscById(page);

			}

			/* sortItem(並べ替え項目)がidやfuriganaでないとき(開発者ツールでクエリパラメータで値を変えられたときなど)は,
			 * 削除済み以外の入荷先情報を入荷先IDで昇順に並べた出荷先一覧を取得する. */
		} else {
			return this.getAllInAscById(page);

		}
	}

	// 削除済み以外の出荷先一覧を出荷先IDの昇順で取得する.
	@Override
	public PageInfo<MCustomer> getAllInAscById(int page) {

		PageHelper.startPage(page, SHOW_SIZE);
		List<MCustomer> customerList = customerMapper.findAllInAscById();

		return new PageInfo<>(customerList);
	}

	// 削除済み以外の出荷先検索結果一覧を取得(出荷先ID・出荷先名・出荷先名ふりがなで検索)し,指定した項目と順序でソートする.
	@Override
	public PageInfo<MCustomer> getSearchResults(
			String search, String sortItem, String sort, int page) {

		PageHelper.startPage(page, SHOW_SIZE);
		List<MCustomer> customerList = customerMapper.findSearchResults(search, sortItem, sort);

		return new PageInfo<>(customerList);
	}

	// 出荷先を登録する.
	@Override
	public void registerOne(MCustomer customer) {

		customerMapper.insertOne(customer);

	}

	// 出荷先IDから出荷先情報を取得する(削除済みは除く).
	@Override
	public MCustomer getByCustomerId(Integer customerId) {

		MCustomer customer = customerMapper.findByCustomerId(customerId);

		return customer;
	}

	// 出荷先の情報を更新する.
	@Override
	public void updateOne(MCustomer customer) {

		customerMapper.updateOne(customer);

	}

	// 削除フラグを更新する.
	@Override
	public void updateIsDeleted(MCustomer customer) {

		// 出荷先情報の削除は物理削除ではなく論理削除のため,削除フラグ(is_deleted)を削除済みの1に変更する.
		customer.setCustomerIsDeleted(1);
		// 削除フラグを更新する.
		customerMapper.updateIsDeleted(customer);

	}

}
