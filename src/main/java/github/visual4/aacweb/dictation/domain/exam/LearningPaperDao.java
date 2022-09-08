package github.visual4.aacweb.dictation.domain.exam;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import github.visual4.aacweb.dictation.Dao;

@Repository
public class LearningPaperDao {

	final SqlSession session;
	
	public LearningPaperDao(SqlSession session) {
		this.session = session;
	}
	
	public void insertLearningPaper(LearningPaper paper) {
		session.insert(Dao.mapper(this, "insertLearningPaper"), paper);
	}
	/**
	 * 어절 학습 이력(보고쓰기x, 학습o, 퀴즈x) - 낱말학습, 문장학습 데이터
	 * @param studentSeq
	 * @return
	 */
	public List<LearningPaper> findLearninsByStudent(Long studentSeq) {
		return session.selectList(Dao.mapper(this, "findLearninsByStudent"), studentSeq);
	}
	
}
