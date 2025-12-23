package com.example.domain.users.model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class MUser {
	
	private Integer userId;								// ユーザーID.
	private String emailAddress;						// メールアドレス.
	private String password;							// パスワード.
	private String familyName;							// ユーザー姓.
	private String firstName;							// ユーザー名.
	private String employeeNumber;						// 従業員番号.
	private Integer isAdmin;							// 管理者権限.
	private Integer userIsDeleted;						// 削除フラグ.
	private LocalDateTime userRegisterDateTime; 		// 登録日.
	private LocalDateTime userUpdateDateTime;			// 更新日.
	private String fullName;							// familyNameとfirstNameを連結した担当者名.

}
