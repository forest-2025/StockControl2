package com.example.form.products.info;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class ImageEditForm {
	private MultipartFile productFile;			// 商品画像(エンティティクラスはproductImage).

}
