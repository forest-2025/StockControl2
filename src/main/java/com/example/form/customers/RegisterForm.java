package com.example.form.customers;

import com.example.validation.ValidGroup1;
import com.example.validation.ValidGroup2;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 出荷先登録フォーム画面の入力値を受け取るフォームクラス.
 * 
 */
@Data
public class RegisterForm {

	@NotBlank(groups = ValidGroup1.class)
	@Size(min = 1, max = 100, groups = ValidGroup2.class)
	private String customerName; // 出荷先名.

	@NotBlank(groups = ValidGroup1.class)
	@Size(min = 1, max = 100, groups = ValidGroup2.class)
	@Pattern(regexp = "^[\\p{InHiragana}ー]+$", groups = ValidGroup2.class)
	private String customerFurigana; // 出荷先名ふりがな.

}

/* ^[\\p{InHiragana}ー]+$でひらがなと長音符号（ー）だけで構成された文字列か確認できる.
 * ^（キャレット） 正規表現の先頭を意味する.
 * [\p{InHiragana}ー] で1文字分の条件を表し,
 * \p{InHiragana} でユニコードのひらがなブロックを表す(ユニコードのひらがなにすることで,ゔ ゑ ゐ等もカバーできる).
 * \pの前にもう一つ\がついているのはエスケープ文字.
 * ー は長音符号でこれはカタカナに分類されるためここで足している.
 * + 直前の文字（この場合 [\\p{InHiragana}ー] の1文字）が1回以上連続することを意味する(1文字以上ということ).
 * $ 正規表現の末尾を意味する. */