package com.example.form.products.info;

import org.springframework.web.multipart.MultipartFile;

import com.example.domain.products.validation.info.ImageExtension;
import com.example.domain.products.validation.info.MaxUploadSizeExceeded;
import com.example.domain.products.validation.info.SupplierIdExists;
import com.example.domain.products.validation.info.UniqueProductNumber;
import com.example.validation.ValidGroup1;
import com.example.validation.ValidGroup2;
import com.example.validation.ValidGroup3;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 商品登録フォーム画面の入力値を受け取るフォームクラス.
 * 
 */
@Data
@UniqueProductNumber(productIdField="productId",productNumberField="productNumber", groups = ValidGroup3.class)
public class RegisterForm {

	private Integer productId;	// 商品ID(バリデーションに必要).
	
	@NotBlank(groups = ValidGroup1.class)
	@Size(min = 1, max = 50, groups = ValidGroup2.class)
	private String productNumber; // 商品番号.

	@NotBlank(groups = ValidGroup1.class)
	@Size(min = 1, max = 100, groups = ValidGroup2.class)
	private String productName; // 商品名.

	@NotNull(groups = ValidGroup1.class)
	@SupplierIdExists(groups = ValidGroup2.class)
	private Integer supplierId; // 入荷先ID.

	@MaxUploadSizeExceeded(groups = ValidGroup1.class)
	@ImageExtension(groups = ValidGroup2.class)
	private MultipartFile productFile; // 商品画像.

}
