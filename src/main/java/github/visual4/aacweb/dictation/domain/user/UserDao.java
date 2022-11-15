package github.visual4.aacweb.dictation.domain.user;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import github.visual4.aacweb.dictation.Dao;
import github.visual4.aacweb.dictation.TypeMap;

@Repository
public class UserDao {

	@Autowired
	SqlSession session;

	public User findBy(User.Column column, Object value) {
		return session.selectOne(Dao.mapper(this, "findBy"),
				TypeMap.with("colname", column.name(), "value", value));
	}

	public void insertUser(User user) {
		session.insert(Dao.mapper(this, "insertUser"), user);
	}

	public List<User> searchUsers(String keyword, TypeMap search) {
		search.put("keyword", keyword + "%");
		return session.selectList(Dao.mapper(this, "searchUsers"), search);
	}
	/**
	 * 카드 결제 심사를 위해 추가함
	 * @param id
	 * @param pass
	 * @return
	 */
	public User loginManually(String id, String pass) {
		return session.selectOne(
				Dao.mapper(this, "loginManually"),
				TypeMap.with("id", id, "pass", pass));
	}
	
	
}
