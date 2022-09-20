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
	/**
	 * 문장, 낱말 시험 결과 조회
	 * 
	 * .....+........+.....+.....
	 *      | 보고쓰기 | 학습 | 퀴즈
	 * .....+........+.....+.....
	 *  낱말     o       o     o
	 *  문장     x       x     o
	 * .....+........+.....+.....
	 * 
	 * @param studentSeq
	 * @return
	 */
	public List<ExamPaper> findExamsByStudent(Long studentSeq) {
		return session.selectList(Dao.mapper(this, "findExamsByStudent"), studentSeq);
	}
	/**
	 * 주어진 학생의 각 section chunk별 최종 시험 점수 조회. 5단계의 경우 60문제이므로 6개의 chunk로 구성됨.
	 * @param studentSeq
	 */
	public List<ExamPaper> findRecentExamsPerSegment(long studentSeq) {
		return session.selectList(Dao.mapper(this, "findRecentExamsPerSegment"), studentSeq);
		
	}

}
