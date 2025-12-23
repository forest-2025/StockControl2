package com.example.form.products.info;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterForm {

	@NotBlank
	@Size(min = 0, max = 50)
	private String productNumber;				// 商品番号.

	@NotBlank
	@Size(min = 0, max = 100)
	private String productName;					// 商品名.

	@NotNull(message = "入荷先名は必ず入力してください")
	private Integer supplierId;					// 入荷先ID.

	private MultipartFile productFile;			// 商品画像(エンティティクラスはimageだが).


}
