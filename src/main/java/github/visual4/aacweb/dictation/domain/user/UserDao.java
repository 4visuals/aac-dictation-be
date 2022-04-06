package github.visual4.aacweb.dictation.domain.user;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import github.visual4.aacweb.dictation.Dao;
import github.visual4.aacweb.dictation.TypeMap;

@Repository
public class UserDao {

	@Autowired
	SqlSession session;

	public User findBy(String column, String value) {
		return session.selectOne(Dao.mapper(this, "findBy"),
				TypeMap.with("colname", column, "value", value));
	}

	public void insertUser(User user) {
		session.insert(Dao.mapper(this, "insertUser"), user);
	}
	
	
}
