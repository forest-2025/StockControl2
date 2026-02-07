package com.example.dto.products;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * t_transaction_history テーブルに対応するクラス.
 * データベースの t_transaction_history テーブルの1レコード分の情報を保持する.
 * 
 */
@Data
public class TTransactionHistory {
	
	 private Integer historyId;								// 入荷・出荷・修正履歴ID.
	 private Integer productId;								// 商品ID.
	 private Integer amountOfChange;						// 在庫の増減数.
	 private Integer supplierId;							// 入荷先ID.
	 private Integer customerId;							// 出荷先ID.
	 private Integer userId;								// ユーザーID.
	 private String remarks;								// 備考.
	 private LocalDateTime historyRegisterDateTime;			// 登録日.
}
