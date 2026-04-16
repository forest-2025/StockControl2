package com.example.form.products.info;

import org.springframework.web.multipart.MultipartFile;

import com.example.domain.products.validation.info.ImageExtension;
import com.example.domain.products.validation.info.MaxUploadSizeExceeded;
import com.example.validation.ValidGroup1;
import com.example.validation.ValidGroup2;

import lombok.Data;

/**
 * 画像修正フォーム画面の入力値を受け取るフォームクラス.
 * 
 */
@Data
public class ImageEditForm {
	
	@MaxUploadSizeExceeded(groups = ValidGroup1.class)
	@ImageExtension(groups = ValidGroup2.class)
	private MultipartFile productFile;			// 商品画像.

}
