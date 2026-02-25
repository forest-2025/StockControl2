package com.example.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.domain.users.model.MUser;

/**
 * ユーザー情報を扱う Mapper.
 * m_user テーブルの情報取得・登録・更新を行う.
 * 
 */
@Mapper
public interface UserMapper {

	/** 
	 * メールアドレスからユーザー情報を取得する(削除済みは除く). 
	 * 
	 * @param emailAddress メールアドレス.
	 * @return ユーザー情報.
	 */
	public MUser findByEmailAddress(String emailAddress);

	/** 
	 * 削除済み以外のユーザー情報を従業員番号の昇順で全件取得する.
	 * 
	 * @return ユーザー情報一覧.
	 */
	public List<MUser> findAll();

	/** 
	 * 削除済み以外のユーザー検索結果一覧を取得する(従業員番号・姓・名・管理者権限で検索する).
	 * 
	 * @param search 検索語句.
	 * @return ユーザー情報一覧.
	 */
	public List<MUser> findSearchResults(String search);

	/** 
	 * 従業員番号からユーザー情報を取得する(削除済みも含む).
	 * 
	 * @param employeeNumber 取得するユーザーの従業員番号.
	 * @return ユーザー情報.
	 */
	public MUser findByEmployeeNumber(String employeeNumber);

	/** 
	 * ユーザーを登録する.
	 * 
	 * @param user 登録するユーザー情報.
	 */
	public void insertOne(MUser user);

	/** 
	 * ユーザーIDからユーザー情報を取得する(削除済みは除く).
	 * 
	 * @param userId 取得するユーザーのID.
	 * @return ユーザー情報.
	 */
	public MUser findByUserId(Integer userId);

	/** 
	 * ユーザーの情報を更新する(パスワード以外).
	 * 
	 * @param user 更新するユーザー情報.
	 */
	public void updateOne(MUser user);

	/** 
	 * ユーザーのパスワードのみ更新する.
	 * 
	 * @param user パスワードを更新するユーザー情報.
	 */
	public void updatePassword(MUser user);

	/** 
	 * ユーザーの削除フラグを更新する.
	 * 
	 * @param user 更新するユーザー情報.
	 */
	public void updateIsDeleted(MUser user);
	

	/** 
	 * 指定した商品番号と重複するデータの件数を取得する.
	 * 登録時はすべてのレコードを対象とし,更新時は商品IDで商品自身を除外して確認する.
	 * 
	 * @param productIdValue 商品ID.
	 * @param productNumberValue 商品番号.
	 * @return 一致するレコード数（0なら重複なし,1以上なら重複あり）.
	 */
	public int countDuplicates(String columnName, Integer userId , String checkItem);
	
}
