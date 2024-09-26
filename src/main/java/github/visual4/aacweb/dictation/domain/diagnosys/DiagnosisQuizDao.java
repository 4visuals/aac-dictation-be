package github.visual4.aacweb.dictation.domain.diagnosys;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import github.visual4.aacweb.dictation.Dao;

@Repository
public class DiagnosisQuizDao {

	final SqlSession session;
	public DiagnosisQuizDao(SqlSession session) {
		this.session = session;
	}
	
	public List<DiagnosisQuiz> listQuiz() {
		return session.selectList(Dao.mapper(this, "listQuiz"));
	}

	public DiagnosisQuiz findBySeq(Integer quizSeq) {
		return Dao.selectOne(session, Dao.mapper(this, "findBySeq"), quizSeq);
	}
	
}
