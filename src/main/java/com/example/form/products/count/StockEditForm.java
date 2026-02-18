package com.example.form.products.count;

import com.example.validation.ValidGroup1;
import com.example.validation.ValidGroup2;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 在庫修正フォーム画面の入力値を受け取るフォームクラス.
 * 
 */
@Data
public class StockEditForm {

	@NotNull(groups = ValidGroup1.class)
	@Min(value = 0, groups = ValidGroup2.class)
	private Integer actualProductCount; // 実在庫数.

	@Size(min = 1, max = 100, groups = ValidGroup1.class)
	private String remarks; // 備考.
}
