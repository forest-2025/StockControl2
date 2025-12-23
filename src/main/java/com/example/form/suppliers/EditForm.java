package com.example.form.suppliers;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EditForm {

	@NotBlank
	@Size(min = 1, max = 100)
	private String supplierName; // 入荷先名.

	@NotBlank
	@Size(min = 1, max = 100)
	@Pattern(regexp = "^[ぁ-んー]+$", message = "ひらがなで入力してください")
	private String supplierFurigana; // 入荷先名ふりがな.
	
}
