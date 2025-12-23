package com.example.form.products.count;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ArriveForm {
	
	@Min(1)
	@NotNull
	private Integer amountOfChange;		// 商品の増減数.
	
	@Size(min = 0, max = 100)
	private String remarks;					// 備考.

}
