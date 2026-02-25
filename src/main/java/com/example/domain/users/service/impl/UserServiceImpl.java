package com.example.domain.users.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.users.model.MUser;
import com.example.domain.users.service.UserService;
import com.example.repository.UserMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

/**
 * UserService の実装クラス.
 * 
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	// メールアドレスからユーザー情報を取得する(削除済みは除く).
	@Override
	public MUser getByEmailAddress(String emailAddress) {

		MUser user = userMapper.findByEmailAddress(emailAddress);

		return user;
	}
	
	// 削除済み以外のユーザー情報を従業員番号の昇順でページングして取得する.
	public PageInfo<MUser> getUsers(int page, int size) {
		
		/* ページング(大量のデータを小分けにして表示・取得する仕組み)には外部ライブラリのPageHelprを使用する.
		 * PageHelperクラスのstartPage(page, size)メソッドでSQLのLIMIT句(取得するデータの上限件数を指定)と,
		 * OFFSET句(データの取得を行う最初の位置を指定)をSQLに適用する準備をする(適用するMapperの直前に呼ばないといけない,
		 * この時点ではSQLはまだ変更されていない).
		 * 内部ではThreadLocal(スレッドローカル スレッド(リクエスト単位の処理を担当する作業単位)専用の一時記憶領域))にページ番号やページサイズなどの情報を保持する. */
        PageHelper.startPage(page, size);

        /* findAll()で削除されていないユーザー情報をすべて取得するがその際にSQLにLIMIT句(LIMIT page)とOFFSET句(OFFSET size)が,
         * 自動的に付与されるため変数sizeのデータの位置から変数pageの件数分を取得するという条件に変更される(実際のMapperを変更せず自動的に行われている).
         * この時点では変数resultにはsizeのデータの位置からpageの件数分だけ取得したデータだけ格納されている.
         * この時格納されているのはArrayListを継承したPage<T>クラスのオブジェクトで,Page<T>でラップすることで元のリスト(ユーザー情報)とページ情報をすべて保持できるようになる.
         * (ArrayListを継承しているためListに格納できる.) */
        List<MUser> result = userMapper.findAll();

        /* PageInfo<T>はページング(大量のデータを小分けにして表示・取得する仕組み)結果をまとめて保持するクラス.
		 * 現在のページ番号・1ページあたりの件数・全件数・総ページ数・前後ページの有無・実データ（List）などの情報をもつことができる.
		 * Mapperから返ってきたList(変数result)をPageInfoにコンストラクタ引数で渡してnewすることで自動でページング情報（総件数や総ページ数など）と,
		 * Listの情報を持つオブジェクトを作成できる(そのオブジェクトがメソッドの戻り値となっている).
		 * new PageInfo<>(result,5)とするとページ番号ナビゲーションの最大表示数を5に変更できる(デフォルトは8).*/
        return new PageInfo<>(result);
    }
	
	// 削除済み以外のユーザー一覧から検索語句が従業員番号・姓・名・管理者権限と一致するユーザーを, 従業員番号の昇順でページングして取得する.
	public PageInfo<MUser> getSearchUsers(int page, int size,String search) {
        
        PageHelper.startPage(page, size);

        List<MUser> result = userMapper.findSearchResults(search);

        return new PageInfo<>(result);
    }
//
//	// DBに登録済みの従業員番号と重複していないか確認する.
//	@Override
//	public boolean isNotDuplicateEmployeeNumber(String employeeNumber) {
//
//		// 変数employeeNumberの従業員番号でユーザーを取得する(従業員番号はユニーク制約なので削除済みのユーザーも含む).
//		MUser user = userMapper.findByEmployeeNumber(employeeNumber);
//
//		// 変数userがnullなら重複する従業員番号がないのでtrueを返し,nullでなければその従業員番号のユーザーが存在する(重複する)のでfalseを返す.
//		if (user == null) {
//			return true;
//		} else {
//			return false;
//		}
//
//	}
//
//	// DB に登録済みのメールアドレスと重複していないか確認する.
//	@Override
//	public boolean isNotDuplicateEmailAddress(String emailAddress) {
//
//		// 変数emailAddressのメールアドレスでユーザーを取得する.
//		MUser user = userMapper.findByEmailAddress(emailAddress);
//
//		// 変数userがnullなら重複するメールアドレスがないのでtrueを返し,nullでなければそのメールアドレスのユーザーが存在する(重複する)のでfalseを返す.
//		if (user == null) {
//			return true;
//		} else {
//			return false;
//		}
//	}
	
	@Override
	public boolean isNotDuplicates(String columnName, Integer userId , String checkItem) {
		
		int count = userMapper.countDuplicates(columnName, userId, checkItem);
		
		if(count == 0) {
			
			return true;
		}
		return false;
	}

	// ユーザーを登録する.
	@Override
	public void registerOne(MUser user) {

		// パスワードをハッシュ化して設定する.
		String password = passwordEncoder.encode(user.getPassword());
		user.setPassword(password);
		
		// ユーザーを登録する.
		userMapper.insertOne(user);
	}

	// ユーザーIDからユーザー情報を取得する(削除済みは除く). 
	@Override
	public MUser getByUserId(Integer userId) {
		MUser user = userMapper.findByUserId(userId);
		return user;
	}

	// ユーザーの情報を更新する(パスワード以外).
	@Override
	public void updateExceptPassword(MUser user) {
		userMapper.updateOne(user);
	}

	// ユーザーのパスワードを更新する. 
	@Override
	public void updatePassword(MUser user) {
		
		// パスワードをハッシュ化して設定する.
		String password = passwordEncoder.encode(user.getPassword());
		user.setPassword(password);
		// パスワードを更新する.
		userMapper.updatePassword(user);
	}
	
	// 削除フラグを更新する.
	@Override
	public void updateIsDeleted(MUser user) {
		
		// ユーザー情報の削除は物理削除ではなく論理削除のため,削除フラグ(is_deleted)を削除済みの1に変更する.
		user.setUserIsDeleted(1);
		// 削除フラグを更新する.
		userMapper.updateIsDeleted(user);
	}

}
