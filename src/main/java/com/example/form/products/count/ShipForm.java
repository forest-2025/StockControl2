package com.example.form.products.count;

import com.example.domain.products.validation.count.CustomerIdExists;
import com.example.domain.products.validation.count.WithinStock;
import com.example.validation.ValidGroup1;
import com.example.validation.ValidGroup2;
import com.example.validation.ValidGroup3;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 出荷フォーム画面の入力値を受け取るフォームクラス.
 * 
 */
@Data
@WithinStock(groups = ValidGroup3.class)
public class ShipForm {

	private Integer productId;	// 商品ID(バリデーションに必要).
	
	@NotNull(groups = ValidGroup1.class)
	@CustomerIdExists(groups = ValidGroup2.class)
	private Integer customerId; // 出荷先ID.

	@NotNull(groups = ValidGroup1.class)
	@Min(value = 1, groups = ValidGroup2.class)
	private Integer amountOfChange; // 商品の増減数.

	@Size(min = 0, max = 100, groups = ValidGroup1.class)
	private String remarks; // 備考.

}
