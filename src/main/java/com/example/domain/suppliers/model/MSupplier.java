package com.example.domain.suppliers.model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class MSupplier {
	
	private Integer supplierId;								// 入荷先ID.
	private String supplierName;							// 入荷先名.
	private String supplierFurigana;						// 入荷先名ふりがな.
	private Integer supplierIsDeleted;						// 入荷先削除フラグ.
	private LocalDateTime supplierRegisterDateTime; 		// 登録日.
	private LocalDateTime supplierUpdateDateTime;			// 更新日.

	
}
