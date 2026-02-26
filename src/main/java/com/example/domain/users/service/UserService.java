package com.example.domain.users.service;

import com.example.domain.users.model.MUser;
import com.github.pagehelper.PageInfo;

/**
 * ユーザーに関する業務処理を提供するサービス.
 * 
 */
public interface UserService {

	/** 
	 * メールアドレスからユーザー情報を取得する(削除済みは除く).
	 * 
	 * @param emailAddress 取得したいユーザーのメールアドレス.
	 * @return ユーザー情報.
	 */
	public MUser getByEmailAddress(String emailAddress);

	/** 
	 * 従業員番号を指定された並べ替え順序（昇順または降順）に基づいて並び替えたユーザー一覧を取得する.
	 *
	 * @param search 検索語句.
	 * @param sort 並べ替え順序（昇順または降順）.
	 * @param page 何ページ目かを表すページ番号（1始まり）.
	 * @return 商品一覧のページ情報.
	 */
	public PageInfo<MUser> findAllSorted(String search, String sort, int page);

	/** 
	 * 削除済み以外のユーザー情報を従業員番号の昇順でページングして取得する.
	 * 
	 * @param page 何ページ目かを表すページ番号（1始まり）.
	 * @return ユーザー一覧のページ情報.
	 */
	public PageInfo<MUser> getUsers(int page);

	/** 
	 * 削除済み以外のユーザー一覧から検索語句が従業員番号・姓・名・管理者権限と一致するユーザーを,
	 * 従業員番号の昇順でページングして取得する.
	 * 
	 * @param search 検索語句.
	 * @param sort 並べ替え順序（昇順または降順）.
	 * @param page 何ページ目かを表すページ番号（1始まり）.
	 * @return ユーザー一覧のページ情報.
	 */
	public PageInfo<MUser> getSearchUsers(String search, String sort, int page);

	/** 
	 * 指定した文字列(checkItem)と重複するデータが存在するか判別する.
	 * 登録時はすべてのレコードを対象とし,更新時はユーザーIDで自身は除外して確認する.
	 * 
	 * @param columnName カラム名(フィールド名とテーブルのカラム名が違うため).
	 * @param userIdValue ユーザーID.
	 * @param checkItemValue 重複があるか判別したいフィールドの値.
	 * @return 重複がなければ true,あれば false.
	 */
	public boolean isNotDuplicates(String columnName, Object userIdValue, Object checkItemValue);

	/** 
	 * ユーザーを登録をする.
	 * 
	 * @param user 登録するユーザー情報.
	 */
	public void registerOne(MUser user);

	/** 
	 * ユーザーIDからユーザー情報を取得する(削除済みは除く). 
	 * 
	 * @param userId 取得するユーザー情報.
	 * @return ユーザー情報.
	 */
	public MUser getByUserId(Integer userId);

	/** ユーザーの情報を更新する(パスワード以外). 
	 * 
	 * @param user 更新するユーザー情報.
	 */
	public void updateExceptPassword(MUser user);

	/** ユーザーのパスワードを更新する. 
	 * 
	 * @param user 更新するユーザー情報(パスワード).
	 */
	public void updatePassword(MUser user);

	/** 
	 * 削除フラグを更新する.
	 * ユーザー情報の削除は物理削除ではなく論理削除のため削除フラグを1に設定する.
	 * 
	 * @param user 更新するユーザー情報.
	 */
	public void updateIsDeleted(MUser user);

}
