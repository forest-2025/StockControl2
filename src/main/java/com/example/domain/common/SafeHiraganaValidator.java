package com.example.domain.common;

import java.text.Normalizer;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * SafeHiragana アノテーションの検証処理を行う.
 * 
 * null の場合は @NotBlank に任す.
 */
public class SafeHiraganaValidator implements ConstraintValidator<SafeHiragana, String> {
	
	/**
	 * 入力された力された文字列がひらがなと長音符(ー)と全角スペースのみで構成されているか確認する.
	 *
	 * @param value 検証対象のオブジェクト.
	 * @param context バリデーションコンテキスト.
	 * @return ひらがなと長音符(ー)と全角スペースだけなら true,そうでなければ false.
	 */
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {

		// 文字列のため空白もチェックする.
		if(value == null || value.isEmpty()) {
			
			return true;
			
		} else {
			
			// 「ゔ」などのひらがながひらがなと濁点の2文字に分けて認識するNFD形式を,ひらがなと濁点が１文字と認識できるNFC形式に変換する.
			String normalized = Normalizer.normalize(value, Normalizer.Form.NFC);

			// 文字列全体が正規表現に一致した場合trueになる.
	        return normalized.matches("^[\\p{IsHiragana}ー　]+$");
		}
	}
}

/* ^[\\p{IsHiragana}ー　]+$でひらがなと長音符号（ー）,「　」だけで構成された文字列か確認できる.
 * ^（キャレット） 正規表現の先頭を意味する
 * [\p{IsHiragana}ー] で1文字分の条件を表し,
 * \p{IsHiragana} でユニコードのスクリプト（文字体系）指定する.
 * (InHiraganaはひらがな用に確保されたU+3040〜U+309Fの範囲をすべてを指すが,将来を見越して空きスペース(未定義領域)があるため,
 * そこを指定されるなど思わぬ動きをしないようにIsHiraganaを使用する).
 * \pの前にもう一つ\がついているのはエスケープ文字.
 * ー は長音符号でこれはカタカナに分類されるためここで足している.
 * 「　」は全角スペースで支店名を入れるときなどで使用できるように足している(ひらがなにゅうりょくのため半角スペースは足さない).
 * + 直前の文字（この場合 [\\p{IsHiragana}ー　] の1文字）が1回以上連続することを意味する(1文字以上ということ).
 * $ 正規表現の末尾を意味する. 
 * 
 * Apple社製品がNFD形式を採用していて,ゔは「う + "(\u3099)」と判断されるがIsHiraganaでは"(\u3099)が対象外である.
 * "(\u3099)と゚  （\u309A）だけ追加するよりNFC形式への変換するほうが安全なためこちらを作成している. */
