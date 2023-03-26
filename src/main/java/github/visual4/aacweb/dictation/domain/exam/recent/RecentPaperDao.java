package github.visual4.aacweb.dictation.domain.exam.recent;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import github.visual4.aacweb.dictation.Dao;
import github.visual4.aacweb.dictation.TypeMap;
import github.visual4.aacweb.dictation.domain.exam.ExamPaper;
import github.visual4.aacweb.dictation.domain.sentence.Sentence.SentenceType;

@Repository
public class RecentPaperDao {

	final SqlSession session;
	
	public RecentPaperDao(SqlSession session) {
		this.session = session;
	}
	
	public void insertWrongAnswer() {
		
	}
	/**
	 * 
	 * @param paper
	 */
	public  void insertRecentPaper(RecentPaper paper) {
		session.insert(Dao.mapper(this, "insertAsSentenceExam"), paper);
	}

	public  RecentPaper findExistingPaper(RecentPaper paper) {
		return session.selectOne(Dao.mapper(this, "findExistingPaper"), paper);
	}

	public void updateForReplacement(RecentPaper paper) {
		session.update(Dao.mapper(this, "updateForReplacement"), paper);	
	}
	/**
	 * 학생이 주어진 section에서 시도한 최근 시험 기록들 
	 * @param studentSeq
	 * @param sectionSeq
	 */
	public List<RecentPaper> findRecentPapers(Long studentSeq, Integer sectionSeq) {
		return session.selectList(Dao.mapper(this, "findRecentPapers"),
				TypeMap.with(
						"student", studentSeq,
						"section", sectionSeq) );
		
	}
	/**
	 * 주어진 학생의 section별 시험 기록 모두 반환
	 * @param studentSeq
	 * @return 
	 */
	public List<RecentPaper> findRecentPapersByStudent(Long studentSeq) {
		return session.selectList(
				Dao.mapper(this, "findRecentPapers"),
				TypeMap.with("student", studentSeq));
	}
}
