package com.example.domain.customers.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.customers.service.CustomerService;
import com.example.dto.customers.MCustomer;
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

	// 削除済み以外の出荷先一覧を出荷先IDの昇順で取得する.
	@Override
	public PageInfo<MCustomer> getAllInAscById(int page, int size) {

		PageHelper.startPage(page, size);
		List<MCustomer> customerList = customerMapper.findAllInAscById();

		return new PageInfo<>(customerList);
	}

	// 削除済み以外の出荷先検索結果一覧を取得(出荷先ID・出荷先名・出荷先名ふりがなで検索する)し指定した項目と順序でソートする.
	@Override
	public PageInfo<MCustomer> getSearchResults(
			int page, int size, String search, String sortItem, String sort) {

		PageHelper.startPage(page, size);
		List<MCustomer> customerList = customerMapper.findSearchResults(search, sortItem, sort);

		return new PageInfo<>(customerList);
	}

	// 出荷先を登録する.
	@Override
	public void registerOne(MCustomer customer) {

		customerMapper.insertOne(customer);

	}

	// 出荷先IDから削除済み以外の出荷先情報を取得する.
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
