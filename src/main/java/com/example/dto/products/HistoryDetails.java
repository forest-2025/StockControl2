package com.example.dto.products;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * t_transaction_history テーブルに,出荷先名と処理の担当者加えたクラス.
 * 商品の入出荷・在庫の修正履歴を表示するため,出荷先名と処理の担当者が追加されている.
 * 
 */
@Data
public class HistoryDetails {
	
	 private Integer transactionHistoryId;					// 入荷・出荷・修正履歴ID.
	 private Integer productId;								// 商品ID.
	 private Integer amountOfChange;						// 在庫の増減数.
	 private Integer supplierId;							// 入荷先ID.
	 private Integer customerId;							// 出荷先ID.
	 private Integer userId;								// ユーザーID.
	 private String remarks;								// 備考.
	 private LocalDateTime historyRegisterDateTime;			// 登録日.
	 private String customerName;							// 出荷先名
	 private String fullName;								// 担当者.

}
