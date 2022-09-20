package github.visual4.aacweb.dictation.domain.exam;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import github.visual4.aacweb.dictation.Dao;
import github.visual4.aacweb.dictation.TypeMap;

@Repository
public class LearningPaperDao {

	final SqlSession session;
	
	public LearningPaperDao(SqlSession session) {
		this.session = session;
	}
	/**
	 * 어절 보고쓰기, 학습 이력 추가
	 * 
	 * .....+........+.....+.....
	 *      | 보고쓰기 | 학습 | 퀴즈
	 * .....+........+.....+.....
	 *  낱말     x       x     x
	 *  문장     o       o     x
	 * .....+........+.....+.....
	 * 
	 * @param paper
	 */
	public void insertLearningPaper(LearningPaper paper) {
		session.insert(Dao.mapper(this, "insertLearningPaper"), paper);
	}
	/**
	 * 어절 보고쓰기, 학습 이력 조회
	 * 
	 * .....+........+.....+.....
	 *      | 보고쓰기 | 학습 | 퀴즈
	 * .....+........+.....+.....
	 *  낱말     x       x     x
	 *  문장     o       o     x
	 * .....+........+.....+.....
	 * 
	 * @param studentSeq
	 * @return
	 */
	public List<LearningPaper> findLearninsByStudent(Long studentSeq) {
		return session.selectList(Dao.mapper(this, "findLearninsByStudent"), studentSeq);
	}
	/**
	 * 주어진 학생의 어절 시험 답안지
	 * @param sectionSeq
	 * @param studentSeq
	 * @return
	 */
	public List<LearningPaper> findLearningPapersBySection(Integer sectionSeq, Long studentSeq) {
		return session.selectList(Dao.mapper(this, "findLearningPapersBySection"), TypeMap.with(
				"section", sectionSeq,
				"student", studentSeq));
	}
	
}
