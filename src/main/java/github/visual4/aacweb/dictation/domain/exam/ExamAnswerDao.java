package github.visual4.aacweb.dictation.domain.exam;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import github.visual4.aacweb.dictation.Dao;

@Repository
public class ExamAnswerDao {

	final SqlSession session;

	public ExamAnswerDao(SqlSession session) {
		super();
		this.session = session;
	}
	
	public void insertExamAnswer(ExamAnswer answer) {
		session.insert(Dao.mapper(this, "insertExamAnswer"), answer);
	}

	public void updateAnalysis(ExamAnswer sbm) {
		Dao.updateOne(session, Dao.mapper(this, "updateAnalysis"), sbm);
	}
	
}
