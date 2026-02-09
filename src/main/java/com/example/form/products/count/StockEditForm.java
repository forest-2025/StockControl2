package com.example.form.products.count;

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
	
	@Min(0)
	@NotNull
	private Integer actualProductCount;		// 実在庫数.
	
	@Size(min = 1, max = 100)
	private String remarks;					// 備考.
}
