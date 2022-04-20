package github.visual4.aacweb.dictation.domain.exam;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import github.visual4.aacweb.dictation.Dao;

@Repository
public class EojeolAnswerDao {

	final SqlSession session;

	public EojeolAnswerDao(SqlSession session) {
		super();
		this.session = session;
	}
	
	public void insertAnswer(List<EojeolAnswer> answers) {
		session.insert(Dao.mapper(this, "insertAnswer"), answers);
	}
	
}
