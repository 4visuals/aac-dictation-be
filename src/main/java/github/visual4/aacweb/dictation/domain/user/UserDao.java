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
	
	public String findPassword(Long userSeq) {
		return session.selectOne(Dao.mapper(this, "findPassword"), userSeq);
	}
	/**
	 * 비밀번호 변경
	 * @param userSeq 
	 * @param newPass - 새로운 비번
	 * @param curPass - 현재 비번
	 * @return
	 */
	public boolean updatePassword(Long userSeq, String newPass, String curPass) {
		TypeMap param = TypeMap.with(
				"userSeq", userSeq,
				"newPass", newPass,
				"curPass", curPass);
		return session.update(Dao.mapper(this, "updatePassword"), param) == 1;
	}
	/**
	 * 회원 삭제
	 * @param seq
	 */
	public void deleteUser(Long teacherSeq) {
		session.delete(Dao.mapper(this, "deleteUser"), teacherSeq);
	}
	
	
}
