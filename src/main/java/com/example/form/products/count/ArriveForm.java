package com.example.form.products.count;

import com.example.validation.ValidGroup1;
import com.example.validation.ValidGroup2;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 入荷フォーム画面の入力値を受け取るフォームクラス.
 * 
 */
@Data
public class ArriveForm {

	@NotNull(groups = ValidGroup1.class)
	@Min(value = 1, groups = ValidGroup2.class)
	private Integer amountOfChange; // 商品の増減数.

	@Size(min = 0, max = 100, groups = ValidGroup1.class)
	private String remarks; // 備考.

}
