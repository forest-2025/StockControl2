package com.example.domain.users.service;

import java.util.List;

import com.example.domain.users.model.MUser;
import com.github.pagehelper.PageInfo;

public interface UserService {
	
	/** メールアドレスからユーザー情報を取得する. */
	public MUser getByEmailAddress(String emailAddress);
	
	/** ユーザー情報全件取得する. */
	public List<MUser> getAll();
	
	/** ユーザー検索結果一覧を取得する(従業員番号・姓・名・管理者権限で検索する). */
	public List<MUser> getSearchUserList(String search);

	/** 従業員番号が重複しているか確認する. */
	public boolean isNotDuplicateEmployeeNumber(String employeeNumber);
	
	/** メールアドレスが重複しているか確認する. */
	public boolean isNotDuplicateEmailAddress(String emailAddress);
	
	/** ユーザーを登録をする */
	public void registerOne(MUser user);
	
	/** ユーザーIDからユーザー情報を取得する(削除済みは除く). */
	public MUser getByUserId(Integer userId);
	
	/** ユーザーIDで指定したユーザーの情報を更新する(パスワード以外). */
	public void updateExceptPassword(MUser user);
	
	/** ユーザーIDで指定したユーザーのパスワードを更新する. */
	public void updatePassword(MUser user);
	
	/** ユーザーIDで指定したユーザーの削除フラグを更新する. */
	public void updateIsDeleted(MUser user);
	
	public PageInfo<MUser> getUsers(int page, int size) ;
	   
	public PageInfo<MUser> getSearchUsers(int page, int size, String search) ;
}
