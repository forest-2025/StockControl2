package com.example.form.products.info;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 商品登録フォーム画面の入力値を受け取るフォームクラス.
 * 
 */
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

	private MultipartFile productFile;			// 商品画像.

}

/* 商品画像は DB では image で,エンティティクラスでは productImage , form クラスでは　productFile なのは,
 * DB ではシンプルな名前のほうが使いやすいからで,エンティティクラスは何の画像かわかるようにするため product をつけている.
 * form ではエンティティクラスと同じ名前にすると, modelMapper.map で値のコピー・変換をするときに自動的にマッピングされて値が入るが,
 * DB の image には一意の名前のファイル名を入れたいので,マッピングされないように form とエンティティクラスで別の名前にしている.
 */