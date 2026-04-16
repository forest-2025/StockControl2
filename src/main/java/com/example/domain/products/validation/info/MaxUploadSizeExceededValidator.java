package com.example.domain.products.validation.info;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import com.example.domain.products.service.ProductInfoService;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * MaxUploadSizeExceeded アノテーションの検証処理を行う.
 * 
 * 画像ファイルががないとき null になる.
 */
public class MaxUploadSizeExceededValidator implements ConstraintValidator<MaxUploadSizeExceeded, MultipartFile> {

	@Autowired
	private ProductInfoService productInfoService;

	/**
	 * 画像ファイルが20MBを超えていないか確認する.
	 *
	 * @param value 検証対象のオブジェクト.
	 * @param context バリデーションコンテキスト.
	 * @return 越えている場合は true,越えていない場合は false.
	 */
	@Override
	public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {

		if (value == null || value.isEmpty()) {

			return true;
		}

		return productInfoService.isValidFileSize(value);
	}
}
