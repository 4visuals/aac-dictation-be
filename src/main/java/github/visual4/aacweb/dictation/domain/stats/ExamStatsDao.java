package github.visual4.aacweb.dictation.domain.stats;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import github.visual4.aacweb.dictation.Dao;
import github.visual4.aacweb.dictation.TypeMap;

@Repository
public class ExamStatsDao {

	final SqlSession session;
	
	public ExamStatsDao(SqlSession session) {
		super();
		this.session = session;
	}

	public List<SectionStats> findSectionStatsByStudent(Long studentSeq) {
		return session.selectList(Dao.mapper(this, "findSectionStatsByStudent"), studentSeq);
	}
	/**
	 * 주어진 학생이 주어진 section을 시도한 퀴즈 결과 조회
	 * @param studentSeq
	 * @param sectionSeq
	 * @return
	 */
	public List<SectionStats> findStatsBySectionAndStudent(Long studentSeq, Integer sectionSeq) {
		return session.selectList(
			Dao.mapper(this, "findStatsBySectionAndStudent"), 
			TypeMap.with("student", studentSeq, "section", sectionSeq));
	}
}
