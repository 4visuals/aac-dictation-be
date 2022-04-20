package github.visual4.aacweb.dictation.domain.exam;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import github.visual4.aacweb.dictation.Dao;

@Repository
public class ExamPaperDao {

	final SqlSession session;
	
	public ExamPaperDao(SqlSession session) {
		super();
		this.session = session;
	}

	public void insertExamPaper(ExamPaper examPaper) {
		session.insert(Dao.mapper(this, "insertExamPaper"), examPaper);
	}

}
