package github.visual4.aacweb.dictation.domain.stats;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import github.visual4.aacweb.dictation.BaseDao;

@Import(ExamStatsDao.class)
class ExamStatsDaoTest extends BaseDao {

	@Autowired
	ExamStatsDao dao;
	
	@Test
	void test() {
		List<SectionStats> stats = dao.findSectionStatsByStudent(30L);
		assertNotNull(stats);
		assertTrue(stats.size() > 0);
		stats.forEach(System.out::println);
	}

}
