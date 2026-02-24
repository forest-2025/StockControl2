package com.example.form.products.count;

import com.example.domain.products.validation.count.RequiresStockUpdate;
import com.example.validation.ValidGroup1;
import com.example.validation.ValidGroup2;
import com.example.validation.ValidGroup3;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 在庫修正フォーム画面の入力値を受け取るフォームクラス.
 * 
 */
@Data
@RequiresStockUpdate(groups = ValidGroup3.class)
public class StockEditForm {

	private Integer productId;	// 商品ID(バリデーションに必要).
	
	@NotNull(groups = ValidGroup1.class)
	@Min(value = 0, groups = ValidGroup2.class)
	private Integer actualProductCount; // 実在庫数.

	@NotBlank(groups = ValidGroup1.class)
	@Size(min = 1, max = 100, groups = ValidGroup2.class)
	private String remarks; // 備考.
}
