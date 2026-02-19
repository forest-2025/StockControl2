package com.example.form.products.info;

import com.example.validation.ValidGroup1;
import com.example.validation.ValidGroup2;

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

	@NotBlank(groups = ValidGroup1.class)
	@Size(min = 1, max = 50, groups = ValidGroup2.class)
	private String productNumber; // 商品番号.

	@NotBlank(groups = ValidGroup1.class)
	@Size(min = 1, max = 100, groups = ValidGroup2.class)
	private String productName; // 商品名.

	@NotNull(groups = ValidGroup1.class)
	private Integer supplierId; // 入荷先ID.

}
