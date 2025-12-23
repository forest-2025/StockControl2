package com.example.domain.product.model;

import java.time.LocalDateTime;

import lombok.Data;

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
