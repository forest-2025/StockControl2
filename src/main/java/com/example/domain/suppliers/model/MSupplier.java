package com.example.domain.suppliers.model;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * m_supplier テーブルに対応するクラス.
 * データベースの m_supplier テーブルの1レコード分の情報を保持する.
 * 
 */
@Data
public class MSupplier {
	
	private Integer supplierId;								// 入荷先ID.
	private String supplierName;							// 入荷先名.
	private String supplierFurigana;						// 入荷先名ふりがな.
	private Integer supplierIsDeleted;						// 入荷先削除フラグ.
	private LocalDateTime supplierRegisterDateTime; 		// 登録日.
	private LocalDateTime supplierUpdateDateTime;			// 更新日.

	
}
