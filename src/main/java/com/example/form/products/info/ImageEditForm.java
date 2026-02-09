package com.example.form.products.info;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

/**
 * 画像修正フォーム画面の入力値を受け取るフォームクラス.
 * 
 */
@Data
public class ImageEditForm {
	private MultipartFile productFile;			// 商品画像.

}
