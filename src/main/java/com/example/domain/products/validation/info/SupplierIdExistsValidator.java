package com.example.domain.products.validation.info;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.domain.products.service.ProductInfoService;

/**
 * SupplierIdExists アノテーションの検証処理を行う.
 * 
 * null の場合は @NotNull に任す.
 */
public class SupplierIdExistsValidator implements ConstraintValidator<SupplierIdExists, Integer>{

	@Autowired 
	private ProductInfoService productInfoService;

	/**
	 * 入荷先IDがデータベースの m_supplier に存在するか確認する.
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
		
		return productInfoService.isRegister(value);
	}
}
