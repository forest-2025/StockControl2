package com.example.domain.suppliers.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.suppliers.service.SupplierService;
import com.example.dto.common.MSupplier;
import com.example.repository.SupplierMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
@Transactional
public class SupplierServiceImpl implements SupplierService {

	@Autowired
	private SupplierMapper supplierMapper;

	/** 削除済み以外の入荷先一覧を入荷先IDの昇順で取得する. */
	@Override
	public PageInfo<MSupplier> getAllInAscById(int page, int size) {

		PageHelper.startPage(page, size);
		List<MSupplier> supplierList = supplierMapper.findAllInAscById();

		return new PageInfo<>(supplierList);
	}

	/** 削除済み以外の入荷先検索結果一覧を取得する(入荷先ID・入荷先名・入荷先名ふりがなで検索する). */
	@Override
	public PageInfo<MSupplier> getSearchResults(
			int page, int size, String search, String sortItem, String sort) {

		PageHelper.startPage(page, size);
		List<MSupplier> supplierList = supplierMapper.findSearchResults(search, sortItem, sort);

		return new PageInfo<>(supplierList);
	}

	/** 入荷先を登録する. */
	@Override
	public void registerOne(MSupplier supplier) {

		supplierMapper.insertOne(supplier);

	}

	/** 入荷先IDから入荷先情報を取得する(削除済みは除く). */
	@Override
	public MSupplier getBySupplierId(Integer supplierId) {

		MSupplier supplier = supplierMapper.findBySupplierId(supplierId);

		return supplier;
	}

	/** 入荷先IDで指定した入荷先の情報を更新する. */
	@Override
	public void updateOne(MSupplier supplier) {

		supplierMapper.updateOne(supplier);

	}

	/** 入荷先IDで指定した削除フラグを更新する. */
	@Override
	public void updateIsDeleted(MSupplier supplier) {

		// 入荷先情報の削除は物理削除ではなく論理削除のため,削除フラグ(is_deleted)を削除済みの1に変更する.
		supplier.setSupplierIsDeleted(1);
		// 削除フラグを更新する.
		supplierMapper.updateIsDeleted(supplier);

	}

}
