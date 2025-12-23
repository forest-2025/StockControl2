package com.example.domain.users.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.users.model.MUser;
import com.example.domain.users.service.UserService;
import com.example.repository.UserMapper;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/** メールアドレスからユーザー情報を取得する(削除済みは除く). */
	@Override
	public MUser getByEmailAddress(String emailAddress) {

		MUser user = userMapper.findByEmailAddress(emailAddress);

		return user;
	}

	/** 削除済み以外のユーザー情報を従業員番号の昇順で全件取得する. */
	@Override
	public List<MUser> getAll() {

		List<MUser> userList = userMapper.findAll();

		return userList;
	}

	/** 削除済み以外のユーザー検索結果一覧を取得する(従業員番号・姓・名・管理者権限で検索する). */
	@Override
	public List<MUser> getSearchUserList(String search) {

		List<MUser> userList = userMapper.findSearchResults(search);

		return userList;
	}

	/** 従業員番号が重複しているか確認する. */
	@Override
	public boolean isNotDuplicateEmployeeNumber(String employeeNumber) {

		// 変数employeeNumberの従業員番号でユーザーを取得する(従業員番号はユニーク制約なので削除済みのユーザーも含む).
		MUser user = userMapper.findByEmployeeNumber(employeeNumber);

		// 変数userがnullなら重複する従業員番号がないのでtrueを返し,nullでなければその従業員番号のユーザーが存在する(重複する)のでfalseを返す.
		if (user == null) {
			return true;
		} else {
			return false;
		}

	}

	/** メールアドレスが重複しているか確認する. */
	@Override
	public boolean isNotDuplicateEmailAddress(String emailAddress) {

		// 変数emailAddressのメールアドレスでユーザーを取得する.
		MUser user = userMapper.findByEmailAddress(emailAddress);

		// 変数userがnullなら重複するメールアドレスがないのでtrueを返し,nullでなければそのメールアドレスのユーザーが存在する(重複する)のでfalseを返す.
		if (user == null) {
			return true;
		} else {
			return false;
		}
	}

	/** ユーザーを登録する */
	@Override
	public void registerOne(MUser user) {

		// パスワードをハッシュ化して設定する.
		String password = passwordEncoder.encode(user.getPassword());
		user.setPassword(password);
		
		// ユーザーを登録する.
		userMapper.insertOne(user);
	}

	/** ユーザーIDからユーザー情報を取得する(削除済みは除く). */
	@Override
	public MUser getByUserId(Integer userId) {
		MUser user = userMapper.findByUserId(userId);
		return user;
	}

	/** ユーザーIDで指定したユーザーの情報を更新する(パスワード以外). */
	@Override
	public void updateExceptPassword(MUser user) {
		userMapper.updateOne(user);
	}

	/** ユーザーIDで指定したユーザーのパスワードを更新する. */
	@Override
	public void updatePassword(MUser user) {
		
		// パスワードをハッシュ化して設定する.
		String password = passwordEncoder.encode(user.getPassword());
		user.setPassword(password);
		// パスワードを更新する.
		userMapper.updatePassword(user);
	}
	
	/** ユーザーIDで指定したユーザーの削除フラグを更新する. */
	@Override
	public void updateIsDeleted(MUser user) {
		
		// ユーザー情報の削除は物理削除ではなく論理削除のため,削除フラグ(is_deleted)を削除済みの1に変更する.
		user.setUserIsDeleted(1);
		// 削除フラグを更新する.
		userMapper.updateIsDeleted(user);
	}

}
