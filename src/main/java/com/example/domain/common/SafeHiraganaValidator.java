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
