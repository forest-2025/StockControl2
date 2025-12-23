package com.example.domain.customers.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.customers.model.MCustomer;
import com.example.domain.customers.service.CustomerService;
import com.example.repository.CustomerMapper;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private CustomerMapper customerMapper;

	/** 削除済み以外の出荷先一覧を出荷先IDの昇順で取得する. */
	@Override
	public List<MCustomer> getAllInAscById() {

		List<MCustomer> customerList = customerMapper.findAllInAscById();

		return customerList;
	}

	/** 削除済み以外の出荷先検索結果一覧を取得する(出荷先ID・出荷先名・出荷先名ふりがなで検索する). */
	@Override
	public List<MCustomer> getSearchResults(String search ,String sortItem,String sort) {

		List<MCustomer> customerList = customerMapper.findSearchResults(search,sortItem,sort);

		return customerList;
	}

	/** 出荷先を登録する. */
	@Override
	public void registerOne(MCustomer customer) {

		customerMapper.insertOne(customer);

	}

	/** 出荷先IDから削除済み以外の出荷先情報を取得する. */
	@Override
	public MCustomer getByCustomerId(Integer customerId) {

		MCustomer customer = customerMapper.findByCustomerId(customerId);

		return customer;
	}

	/** 出荷先IDで指定した出荷先の情報を更新する. */
	@Override
	public void updateOne(MCustomer customer) {

		customerMapper.updateOne(customer);

	}

	/** 出荷先IDで指定した削除フラグを更新する. */
	@Override
	public void updateIsDeleted(MCustomer customer) {

		// 出荷先情報の削除は物理削除ではなく論理削除のため,削除フラグ(is_deleted)を削除済みの1に変更する.
		customer.setCustomerIsDeleted(1);
		// 削除フラグを更新する.
		customerMapper.updateIsDeleted(customer);

	}

}
