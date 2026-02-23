package com.example.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.domain.products.service.ProductCountService;

/**
 * CustomerIdExists アノテーションの検証処理を行う.
 * 
 * null の場合は @NotNull に任す.
 */
public class CustomerIdExistsValidator implements ConstraintValidator<CustomerIdExists, Integer> {

	@Autowired 
	private ProductCountService productCountService;

	/**
	 * 出荷先IDがデータベースの m_customer に存在するか確認する.
	 *
	 * @param value 検証対象のオブジェクト.
	 * @param context バリデーションコンテキスト.
	 * @return 存在する場合は true,存在しない場合は false.
	 */
	@Override
	public boolean isValid(Integer value, ConstraintValidatorContext context) {
		
		if (value == null) {
			return true;
		} 
		
		return productCountService.existsByCustomerId(value);
	}
}
