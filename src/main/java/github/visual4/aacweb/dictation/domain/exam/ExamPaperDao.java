package github.visual4.aacweb.dictation.domain.exam;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import github.visual4.aacweb.dictation.Dao;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.domain.sentence.Sentence.SentenceType;

@Repository
public class ExamPaperDao {

	final SqlSession session;
	
	public ExamPaperDao(SqlSession session) {
		super();
		this.session = session;
	}

	public List<ExamPaper> findExamBySectionAndStudent (Integer sectionSeq, SentenceType type, Long studentSeq) {
		return session.selectList(Dao.mapper(this, "findExamBySectionAndStudent"),
				TypeMap.with("section", sectionSeq, "type", type, "student", studentSeq));
	}
	public void insertExamPaper(ExamPaper examPaper) {
		session.insert(Dao.mapper(this, "insertExamPaper"), examPaper);
	}

}
