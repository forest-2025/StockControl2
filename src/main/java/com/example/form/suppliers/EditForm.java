package com.example.form.suppliers;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 *  入荷先情報修正フォーム画面の入力値を受け取るフォームクラス.
 *  
 */
@Data
public class EditForm {

	@NotBlank
	@Size(min = 1, max = 100)
	private String supplierName; // 入荷先名.

	@NotBlank
	@Size(min = 1, max = 100)
	@Pattern(regexp = "^[\\p{InHiragana}ー]+$", message = "ひらがなで入力してください")
	private String supplierFurigana; // 入荷先名ふりがな.
	
}
