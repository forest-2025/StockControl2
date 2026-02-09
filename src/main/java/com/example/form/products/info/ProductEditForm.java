package com.example.form.products.info;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 *  商品情報修正フォーム画面の入力値を受け取るフォームクラス.
 *  
 */
@Data
public class ProductEditForm {

	@NotBlank
	@Size(min = 0,max = 50)
	private String productNumber; // 商品番号.

	@NotBlank
	@Size(min = 0,max = 100)
	private String productName; // 商品名.

	@NotNull(message="入荷先名は必ず入力してください")
	private Integer supplierId; // 入荷先ID.
	

}
