package com.example.form.suppliers;

import com.example.domain.common.SafeHiragana;
import com.example.validation.ValidGroup1;
import com.example.validation.ValidGroup2;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 *  入荷先情報修正フォーム画面の入力値を受け取るフォームクラス.
 *  
 */
@Data
public class EditForm {

	@NotBlank(groups = ValidGroup1.class)
	@Size(min = 1, max = 100, groups = ValidGroup2.class)
	private String supplierName; // 入荷先名.

	@NotBlank(groups = ValidGroup1.class)
	@Size(min = 1, max = 100, groups = ValidGroup2.class)
	@SafeHiragana(groups = ValidGroup2.class)
	private String supplierFurigana; // 入荷先名ふりがな.

}
