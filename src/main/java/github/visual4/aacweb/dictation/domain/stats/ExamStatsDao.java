package github.visual4.aacweb.dictation.domain.stats;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import github.visual4.aacweb.dictation.Dao;

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
}
