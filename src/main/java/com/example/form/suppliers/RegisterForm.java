package com.example.form.suppliers;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterForm {

	@NotBlank
	@Size(min = 1, max = 100)
	private String supplierName; // 入荷先名.

	@NotBlank
	@Size(min = 1, max = 100)
	@Pattern(regexp = "^[\\p{InHiragana}ー]+$", message = "ひらがなで入力してください")
	/* 正規表現: ^[\\p{InHiragana}ー]+$

^（キャレット）

正規表現の 先頭を意味するアンカー

「文字列の最初からマッチする」という意味

例：^a → 文字列の先頭が a のときだけマッチ

[...]（角括弧）

文字クラス と呼ばれる部分

中に書いたどれか1文字にマッチする

例：[abc] → a または b または c にマッチ

今回は [\\p{InHiragana}ー] なので「ひらがなブロック全体 または 長音 ー」にマッチ

\\p{InHiragana}

Unicodeプロパティ

\p{InHiragana} で Unicode の ひらがなブロック全体 にマッチ

例：あ、ゔ、ゐ なども含む

Javaでは正規表現で \ を書くときに エスケープが必要なので \\ と書く

だから \\p{InHiragana} になっている

ー

文字クラスに追加された 長音符号

\p{InHiragana} には含まれないので明示的に書く必要がある

+

直前の文字（この場合 [\\p{InHiragana}ー] の1文字）が 1回以上連続 することを意味

$（ドル）

正規表現の 末尾を意味するアンカー

「文字列の最後までマッチする」という意味

つまり ^ と $ があることで 文字列全体がひらがなだけであること をチェックできる */
	private String supplierFurigana; // 入荷先名ふりがな.

}
