package com.example.domain.customers.model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class MCustomer {

	private Integer customerId; 						// 出荷先ID.
	private String customerName;						// 出荷先名.
	private String customerFurigana;					// 出荷先名ふりがな.
	private Integer customerIsDeleted;					// 出荷先削除フラグ.
	private LocalDateTime customerRegisterDateTime;		// 登録日.
	private LocalDateTime customerUpdateDateTime;		// 更新日.

}
