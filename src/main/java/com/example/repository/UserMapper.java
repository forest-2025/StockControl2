package com.example.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.domain.users.model.MUser;

@Mapper
public interface UserMapper {

	/** メールアドレスからユーザー情報を取得する(削除済みは除く). */
	public MUser findByEmailAddress(String emailAddress);

	/** 削除済み以外のユーザー情報を従業員番号の昇順で全件取得する. */
	public List<MUser> findAll();

	/** 削除済み以外のユーザー検索結果一覧を取得する(従業員番号・姓・名・管理者権限で検索する). */
	public List<MUser> findSearchResults(String search);

	/** 従業員番号からユーザー情報を取得する(削除済みも含む). */
	public MUser findByEmployeeNumber(String employeeNumber);

	/** ユーザーを登録する. */
	public void insertOne(MUser user);

	/** ユーザーIDからユーザー情報を取得する(削除済みは除く). */
	public MUser findByUserId(Integer userId);

	/** ユーザーIDで指定したユーザーの情報を更新する(パスワード以外). */
	public void updateOne(MUser user);

	/** ユーザーIDで指定したユーザーのパスワードのみ更新する. */
	public void updatePassword(MUser user);

	/** ユーザーIDで指定したユーザーの削除フラグを更新する. */
	public void updateIsDeleted(MUser user);
}
