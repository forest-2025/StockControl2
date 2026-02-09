package com.example.form.products.count;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 出荷フォーム画面の入力値を受け取るフォームクラス.
 * 
 */
@Data
public class ShipForm {
	
	@NotNull(message="出荷先名は必ず入力してください")
	private Integer customerId;				// 出荷先ID.
	
	@Min(1)
	@NotNull
	private Integer amountOfChange;			// 商品の増減数.
	
	@Size(min = 0, max = 100)
	private String remarks;					// 備考.

}



